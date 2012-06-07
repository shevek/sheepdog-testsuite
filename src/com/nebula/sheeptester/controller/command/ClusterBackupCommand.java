/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.SheepScanOperator;
import com.nebula.sheeptester.target.operator.SheepScanOperator.FileMetadata;
import com.nebula.sheeptester.target.operator.SheepScanOperator.ScanResponse;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "cluster-backup")
public class ClusterBackupCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(ClusterBackupCommand.class);

    private static class Block {

        private final Sheep sheep;
        private final SheepScanOperator.FileMetadata metadata;

        public Block(Sheep sheep, FileMetadata metadata) {
            this.sheep = sheep;
            this.metadata = metadata;
        }

        public Host getHost() {
            return getSheep().getHost();
        }

        public Sheep getSheep() {
            return sheep;
        }

        public String getPath() {
            return metadata.getFile().getPath();
        }

        public String getName() {
            return metadata.getFile().getName();
        }

        public int getEpoch() {
            return metadata.getEpoch();
        }

        @Nonnegative
        public int getBlockType() {
            String name = getName();
            return Integer.parseInt(name.substring(0, 2), 16);
        }

        @Nonnull
        public String getVdiHash() {
            String name = getName();
            return name.substring(2, 8);
        }

        @Nonnegative
        public int getBlockIndex() {
            String name = getName();
            return Integer.parseInt(name.substring(8), 16);
        }

        @Override
        public String toString() {
            return "Block<" + getName() + ">";
        }
    }

    private static class BlockList {

        private Block metadata;
        private final List<Block> data = new ArrayList<Block>();

        private boolean isNewer(@Nonnull Block repl, @CheckForNull Block curr) {
            if (curr == null)
                return true;
            return curr.getEpoch() <= repl.getEpoch();
        }

        public void addBlock(Block block) {
            switch (block.getBlockType()) {
                case 0x00:
                    int index = block.getBlockIndex();
                    if (index >= data.size()) {
                        for (int i = data.size(); i <= index; i++)
                            data.add(null);
                        data.set(index, block);
                    } else if (isNewer(block, data.get(index))) {
                        data.set(index, block);
                    }
                    break;
                case 0x80:
                    if (isNewer(block, metadata))
                        metadata = block;
                    break;
            }
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            if (metadata == null)
                buf.append("<null-metadata>");
            else
                buf.append(metadata);
            buf.append(": ").append(data.size()).append(" blocks");
            int missing = CollectionUtils.countMatches(data, PredicateUtils.nullPredicate());
            if (missing > 0)
                buf.append(" (").append(missing).append(" missing)");
            return buf.toString();
        }
    }
    @Attribute
    private String directory;

    @Override
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        List<Sheep> sheeps = new ArrayList<Sheep>();
        for (Sheep sheep : context.getSheep().values())
            if (sheep.isRunning())
                sheeps.add(sheep);

        final Map<Sheep, ScanResponse> responses = new ConcurrentHashMap<Sheep, ScanResponse>();
        ControllerExecutor executor = context.newExecutor(sheeps.size());
        for (final Sheep sheep : sheeps) {
            executor.submit("Scanning blocks on sheep " + sheep, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    LOG.info("Retrieving block list from " + sheep);
                    Host host = sheep.getHost();
                    ScanResponse response = (ScanResponse) context.execute(host, new SheepScanOperator(sheep.getConfig().getDirectory(), false));
                    responses.put(sheep, response);
                    LOG.info("Done retrieving block list from " + sheep);
                }
            });
        }
        executor.await();

        LOG.info("Sorting block list.");

        final Map<String, BlockList> blocks = new HashMap<String, BlockList>();
        for (Map.Entry<Sheep, ScanResponse> e : responses.entrySet()) {
            Sheep sheep = e.getKey();
            ScanResponse response = e.getValue();

            for (SheepScanOperator.FileMetadata metadata : response.getResults()) {
                Block block = new Block(sheep, metadata);
                BlockList list = blocks.get(block.getVdiHash());
                if (list == null) {
                    list = new BlockList();
                    blocks.put(block.getVdiHash(), list);
                }
                list.addBlock(block);
            }
        }

        LOG.info("Got " + blocks);

        Collection<? extends Host> hosts = context.getHosts();
        final Map<Host, ChannelSftp> sftps = new ConcurrentHashMap<Host, ChannelSftp>();
        executor = context.newExecutor(hosts.size());
        for (final Host host : hosts) {
            executor.submit("Connecting to sftp on host " + host, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    LOG.info("Connecting to sftp on " + host);
                    Session session = host.getSession();
                    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                    sftp.connect();
                    sftps.put(host, sftp);
                    LOG.info("Connected to sftp on " + host);
                }
            });
        }
        executor.await();

        for (Map.Entry<String, BlockList> e : blocks.entrySet()) {
            BlockList list = e.getValue();
            if (list.metadata == null) {
                LOG.warn("Cannot retrieve " + list + ": no metadata.");
                continue;
            }
            String vdi_name;
            NAME:
            {
                ChannelSftp sftp = sftps.get(list.metadata.getHost());
                try {
                    InputStream in = sftp.get(list.metadata.getPath());
                    try {
                        byte[] vdi_bytes = IOUtils.toByteArray(in, 256);
                        int len = 0;
                        while (len < vdi_bytes.length)
                            if (vdi_bytes[len] == 0)
                                break;
                        if (len > 0)
                            vdi_name = new String(vdi_bytes, 0, len, "US-ASCII");
                        else
                            vdi_name = "unnamed-vdi-" + list.metadata.getName() + "-" + System.currentTimeMillis();
                        LOG.info("Found VDI name " + vdi_name);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                } catch (IOException _e) {
                    LOG.error("Cannot retrieve " + list, _e);
                    continue;
                } catch (SftpException _e) {
                    LOG.error("Cannot retrieve " + list, _e);
                    continue;
                }
            }

            File output = new File(directory + File.separator + vdi_name);
            if (output.isDirectory())
                output = new File(output, "/zero-length-name-vdi-" + list.metadata.getName() + "-" + System.currentTimeMillis());
            LOG.info("Backing up " + vdi_name + " to " + output);
            RandomAccessFile out;
            try {
                FileOutputStream tmp = FileUtils.openOutputStream(output);
                tmp.close();
                out = new RandomAccessFile(output, "rw");
            } catch (IOException _e) {
                LOG.error("Failed to open " + output, _e);
                continue;
            }

            final long BLOCK_SIZE = 4 * 1024 * 1024;

            try {
                DATA:
                {
                    for (Block block : list.data) {
                        long pos = out.getFilePointer();
                        if (block == null) {
                            out.seek(pos + BLOCK_SIZE);
                            continue;
                        } else {
                            try {
                                ChannelSftp sftp = sftps.get(block.getHost());
                                InputStream in = sftp.get(block.getPath());
                                copy(in, out);
                            } catch (Exception _e) {
                                LOG.error("Failed to sftp-get " + block, _e);
                                out.seek(pos + BLOCK_SIZE);
                                continue;
                            }
                        }
                    }
                }
            } catch (IOException _e) {
                LOG.error("Failed to seek in " + out, _e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }


        for (final ChannelSftp sftp : sftps.values()) {
            sftp.disconnect();
        }
    }

    private long copy(InputStream input, DataOutput output) throws IOException {
        byte[] buffer = new byte[16384];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}

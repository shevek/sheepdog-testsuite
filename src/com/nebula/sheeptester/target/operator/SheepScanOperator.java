/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepScanOperator extends AbstractOperator {

    private static final Log LOG = LogFactory.getLog(SheepScanOperator.class);

    public static class FileMetadata {

        private File file;
        private long length;
        private int epoch = -1;
        private String md5;

        public FileMetadata() {
        }

        public FileMetadata(@Nonnull File file, int epoch, byte[] md5) {
            this.file = file;
            this.length = file.length();
            this.epoch = epoch;
            this.md5 = Base64.encodeBase64String(md5);
        }

        @Nonnull
        public File getFile() {
            return file;
        }

        public long getLength() {
            return length;
        }

        @Nonnull
        public String getBlockId() {
            return getFile().getName();
        }

        @CheckForSigned
        public int getEpoch() {
            return epoch;
        }

        @CheckForNull
        public byte[] getMd5() {
            if (md5 == null)
                return null;
            return Base64.decodeBase64(md5);
        }
    }

    private class Walker extends DirectoryWalker<Void> {

        private final List<FileMetadata> results = new ArrayList<FileMetadata>();
        private int epoch;

        public void walk() throws IOException {
            super.walk(new File(directory, "obj"), null);
        }

        @Override
        protected boolean handleDirectory(File directory, int depth, Collection<Void> results) throws IOException {
            String name = directory.getName();
            if (name.startsWith("."))
                return false;
            // LOG.info("Handling name " + name);
            return super.handleDirectory(directory, depth, results);
        }

        @Override
        protected void handleDirectoryStart(File directory, int depth, Collection<Void> results) throws IOException {
            super.handleDirectoryStart(directory, depth, results);
            String name = directory.getName();
            try {
                epoch = Integer.parseInt(name);
            } catch (NumberFormatException e) {
                epoch = -1;
            }
        }

        @Override
        protected void handleDirectoryEnd(File directory, int depth, Collection<Void> results) throws IOException {
            epoch = -1;
            super.handleDirectoryEnd(directory, depth, results);
        }

        @Override
        protected void handleFile(File file, int depth, Collection<Void> out) throws IOException {
            try {
                byte[] md5 = null;
                if (checksum) {
                    FileInputStream fis = FileUtils.openInputStream(file);
                    try {
                        md5 = DigestUtils.md5(fis);
                    } finally {
                        IOUtils.closeQuietly(fis);
                    }
                }
                results.add(new FileMetadata(file, epoch, md5));
            } catch (IOException e) {
                LOG.warn("Failed to read " + file + " during md5 scan: " + e);
            }
        }
    }

    public static class ScanResponse extends AbstractResponse {

        private List<FileMetadata> results;

        public ScanResponse(Operator operator, @Nonnull List<FileMetadata> results) {
            super(operator);
            this.results = results;
        }

        public ScanResponse() {
        }

        @Nonnull
        public List<FileMetadata> getResults() {
            return results;
        }
    }
    private String directory;
    private boolean checksum;

    public SheepScanOperator(String directory, boolean checksum) {
        this.directory = directory;
        this.checksum = checksum;
    }

    public SheepScanOperator() {
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        TimedProcess process = new TimedProcess(context, 5000, "sudo", "chmod", "-R", "a+rX", directory) {

            @Override
            protected void init(Executor executor) {
                super.init(executor);
                // If sheepdog is still cleaning up, we can get a failure from chmod.
                executor.setExitValues(null);
            }
        };
        process.setSilent(true);
        process.execute();

        Walker walker = new Walker();
        walker.walk();
        return new ScanResponse(this, walker.results);
    }
}

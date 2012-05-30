/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerAssertionException;
import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.SheepScanOperator;
import com.nebula.sheeptester.target.operator.SheepScanOperator.ScanResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.collections15.map.LazyMap;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sheep-scan")
public class SheepScanCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(SheepScanCommand.class);

    private static class Block {

        private final String name;
        private final byte[] md5;
        private final List<Sheep> sheeps = new ArrayList<Sheep>();

        public Block(String name, byte[] md5) {
            this.name = name;
            this.md5 = md5;
        }

        public int getCopies() {
            return sheeps.size();
        }

        @Override
        public String toString() {
            return name + "[md5=" + Hex.encodeHexString(md5) + "] on " + sheeps;
        }
    }
    @Attribute(required = false)
    private int copies = 0;

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
                    Host host = sheep.getHost();
                    ScanResponse response = (ScanResponse) context.execute(host, new SheepScanOperator(sheep.getConfig().getDirectory()));
                    responses.put(sheep, response);
                }
            });
        }
        executor.await();

        List<String> errors = new ArrayList<String>();
        Map<String, Block> blocks = new HashMap<String, Block>();
        for (Map.Entry<Sheep, ScanResponse> e : responses.entrySet()) {
            for (Map.Entry<String, byte[]> f : e.getValue().getResults().entrySet()) {
                Block block = blocks.get(f.getKey());
                if (block == null) {
                    block = new Block(f.getKey(), f.getValue());
                    blocks.put(f.getKey(), block);
                } else {
                    if (!Arrays.equals(block.md5, f.getValue()))
                        errors.add("md5sum mismatch between " + block
                                + " and " + new Block(f.getKey(), f.getValue())
                                + " on " + e.getKey());
                }
                block.sheeps.add(e.getKey());
            }
        }

        Map<Integer, MutableInt> counts = new TreeMap<Integer, MutableInt>();
        counts = LazyMap.decorate(counts, new InstantiateFactory(MutableInt.class));
        for (Map.Entry<String, Block> e : blocks.entrySet()) {
            Block block = e.getValue();
            counts.get(block.getCopies()).increment();
            if (copies > 0)
                if (block.getCopies() != copies)
                    errors.add("Wrong number of replicas for " + e.getKey() + ": " + block.getCopies() + " on " + block.sheeps);
        }
        for (Map.Entry<Integer, MutableInt> e : counts.entrySet()) {
            LOG.info("Block replica-count = " + e.getKey() + ": " + e.getValue() + " blocks.");
        }

        if (!errors.isEmpty())
            throw new ControllerAssertionException("Block scan errors found: " + errors);
    }
}

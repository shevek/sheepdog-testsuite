/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

    private class Walker extends DirectoryWalker<Void> {

        private final Map<String, byte[]> results = new HashMap<String, byte[]>();

        public void walk() throws IOException {
            super.walk(new File(directory, "obj"), null);
        }

        @Override
        protected boolean handleDirectory(File directory, int depth, Collection<Void> results) throws IOException {
            if (directory.getName().startsWith("."))
                return false;
            return super.handleDirectory(directory, depth, results);
        }

        @Override
        protected void handleFile(File file, int depth, Collection<Void> out) throws IOException {
            try {
                FileInputStream fis = FileUtils.openInputStream(file);
                try {
                    byte[] md5 = DigestUtils.md5(fis);
                    if (results.containsKey(file.getName()))
                        throw new IllegalStateException("Duplicate block on single sheep: " + file);
                    results.put(file.getName(), md5);
                } finally {
                    IOUtils.closeQuietly(fis);
                }
            } catch (IOException e) {
                LOG.warn("Failed to read " + file + " during md5 scan: " + e);
            }
        }
    }

    public static class ScanResponse extends AbstractResponse {

        private Map<String, byte[]> results;

        public ScanResponse(Operator operator, Map<String, byte[]> results) {
            super(operator);
            this.results = results;
        }

        public ScanResponse() {
        }

        public Map<String, byte[]> getResults() {
            return results;
        }
    }
    private String directory;

    public SheepScanOperator(String directory) {
        this.directory = directory;
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
        process.execute();

        Walker walker = new Walker();
        walker.walk();
        return new ScanResponse(this, walker.results);
    }
}

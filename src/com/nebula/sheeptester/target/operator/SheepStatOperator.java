/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepStatOperator extends AbstractOperator {

    private static final Log LOG = LogFactory.getLog(SheepStatOperator.class);

    public static class StatResponse extends AbstractResponse {

        public boolean hasCore;
        public boolean hasLeaks;

        public StatResponse(Operator operator) {
            super(operator);
        }

        public StatResponse() {
        }
    }
    public String directory;

    public SheepStatOperator(String directory) {
        this.directory = directory;
    }

    public SheepStatOperator() {
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        StatResponse response = new StatResponse(this);

        File dir = new File(directory);
        if (dir.isDirectory()) {
            TimedProcess process = new TimedProcess(context, 5000, "sudo", "chmod", "-R", "a+rX", directory);
            process.execute();

            response.hasCore = new File(dir, "core").exists();
            File valgrind = new File(dir, "/valgrind.out");
            if (valgrind.exists()) {
                InputStream is = FileUtils.openInputStream(valgrind);
                try {
                    LineIterator iterator = new LineIterator(new InputStreamReader(is)) {

                        @Override
                        protected boolean isValidLine(String line) {
                            return line.contains("are definitely lost");
                        }
                    };
                    response.hasLeaks = iterator.hasNext();
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        return response;
    }
}

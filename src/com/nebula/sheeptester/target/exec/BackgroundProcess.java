/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.exec;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class BackgroundProcess extends TargetProcess {

    private static final Log LOG = LogFactory.getLog(BackgroundProcess.class);

    private static class Handler implements ExecuteResultHandler {

        @Override
        public void onProcessComplete(int exitValue) {
            LOG.error("Process exited with result code " + exitValue);
        }

        @Override
        public void onProcessFailed(ExecuteException e) {
            LOG.error("Process failed", e);
        }
    }

    public BackgroundProcess(TargetContext context, String... command) {
        super(context, command);
    }

    @Override
    protected void execute(Executor executor, CommandLine commandline) throws TargetException, IOException {
        executor.execute(commandline, new Handler());
    }
}

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

    private class Handler implements ExecuteResultHandler {

        private final CommandLine commandline;

        public Handler(CommandLine commandline) {
            this.commandline = commandline;
        }

        @Override
        public void onProcessComplete(int exitValue) {
            if (exitValue == 0)
                LOG.info(getContext().getHostId() + ": Process done: " + commandline);
            else
                LOG.info(getContext().getHostId() + ": Process exited with result code " + exitValue + ": " + commandline);
        }

        @Override
        public void onProcessFailed(ExecuteException e) {
            String reason = "";
            switch (e.getExitValue()) {
                case 137:
                    reason = "(killed) ";
                    e = null;
                    break;
                case 143:
                    reason = "(timeout) ";
                    e = null;
                    break;
            }
            LOG.error(getContext().getHostId() + ": Process failed: " + reason + commandline, e);
        }
    }

    public BackgroundProcess(TargetContext context, String... command) {
        super(context, command);
    }

    @Override
    protected void execute(Executor executor, CommandLine commandline) throws TargetException, IOException {
        executor.execute(commandline, new Handler(commandline));
    }
}

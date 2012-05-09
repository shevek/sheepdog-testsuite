/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepKillOperator extends AbstractProcessOperator {

    private static final Log LOG = LogFactory.getLog(SheepKillOperator.class);
    private int pid;

    public SheepKillOperator() {
    }

    public SheepKillOperator(int pid) {
        this.pid = pid;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        if (pid < 0)
            return new TimedProcess(context, 500, "sudo", "killall", "-w", "sheep") {

                @Override
                protected void execute(Executor executor, CommandLine commandline) throws TargetException, IOException {
                    try {
                        executor.execute(commandline);
                    } catch (ExecuteException e) {
                        LOG.warn("killall failed: " + e);
                    }
                }
            };
        else
            return new TimedProcess(context, 500, "sudo", "kill", "-9", String.valueOf(pid));
    }
}

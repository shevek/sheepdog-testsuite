/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.target.exec.TimedProcess;
import org.apache.commons.exec.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepKillOperator extends AbstractProcessOperator {

    private static class SheepKillProcess extends TimedProcess {

        public SheepKillProcess(TargetContext context, String... command) {
            super(context, 500, command);
        }

        @Override
        protected void init(Executor executor) {
            super.init(executor);
            executor.setExitValues(null);
        }
    }
    private static final Log LOG = LogFactory.getLog(SheepKillOperator.class);
    private int pid;

    public SheepKillOperator() {
    }

    public SheepKillOperator(int pid) {
        this.pid = pid;
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        Response response = super.run(context);

        if (pid < 0) {
            // We might have left something in valgrind.
            TargetProcess process = new SheepKillProcess(context, "sudo", "pkill", "-f", "valgrind.*" + context.getSheep());
            process.execute();
        }

        return response;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        if (pid < 0)
            return new SheepKillProcess(context, "sudo", "killall", "-s9", "-w", "sheep");
        else
            return new SheepKillProcess(context, "sudo", "kill", "-9", String.valueOf(pid));
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.exec;

import com.nebula.sheeptester.target.TargetContext;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class TimedProcess extends TargetProcess {

    private static final Log LOG = LogFactory.getLog(TimedProcess.class);
    private final long msecs;

    public TimedProcess(TargetContext context, long msecs, String... command) {
        super(context, command);
        this.msecs = msecs;
    }

    @Override
    protected void init(Executor executor) {
        super.init(executor);
        executor.setWatchdog(new ExecuteWatchdog(msecs));
    }
}
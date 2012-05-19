/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.target.exec.TimedProcess;

/**
 *
 * @author shevek
 */
public class ExecOperator extends AbstractProcessOperator {

    private int timeout;
    private String[] command;

    public ExecOperator() {
    }

    public ExecOperator(int timeout, String... command) {
        this.timeout = timeout;
        this.command = command;
    }

    public ExecOperator(String... command) {
        this(-1, command);
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        if (timeout > -1)
            return new TimedProcess(context, timeout, command);
        else
            return new TargetProcess(context, command);
    }
}

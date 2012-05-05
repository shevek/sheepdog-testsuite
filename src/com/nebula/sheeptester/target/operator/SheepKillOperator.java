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
public class SheepKillOperator extends AbstractProcessOperator {

    private int pid;

    public SheepKillOperator() {
    }

    public SheepKillOperator(int pid) {
        this.pid = pid;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 500, "kill", "-9", String.valueOf(pid));
    }
}

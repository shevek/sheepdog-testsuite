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
public class ClusterShutdownOperator extends AbstractProcessOperator {

    private int port;

    public ClusterShutdownOperator() {
    }

    public ClusterShutdownOperator(int port) {
        this.port = port;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 5000, context.getCollie(), "cluster", "shutdown", "-p", String.valueOf(port));
    }
}

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
public class NodeInfoOperator extends AbstractProcessOperator {

    private int port;

    public NodeInfoOperator() {
    }

    public NodeInfoOperator(int port) {
        this.port = port;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 1000, context.getCollie() + "node", "info", "-p", String.valueOf(port));
    }
}

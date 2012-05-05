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
public class NodeListOperator extends AbstractProcessOperator {

    private int port;

    public NodeListOperator() {
    }

    public NodeListOperator(int port) {
        this.port = port;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 1000, context.getCollie() + "node", "list", "-p", String.valueOf(port));
    }
}

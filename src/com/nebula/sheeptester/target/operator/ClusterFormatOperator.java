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
public class ClusterFormatOperator extends AbstractProcessOperator {

    private int port;
    private long copies;

    public ClusterFormatOperator() {
    }

    public ClusterFormatOperator(int port, long copies) {
        this.port = port;
        this.copies = copies;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 5000, context.getCollie(), "cluster", "format", "-c", String.valueOf(copies), "-p", String.valueOf(port));
    }
}

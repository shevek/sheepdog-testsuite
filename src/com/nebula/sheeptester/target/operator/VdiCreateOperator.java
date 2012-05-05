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
public class VdiCreateOperator extends AbstractProcessOperator {

    private int port;
    private String vdi;
    private long size;

    public VdiCreateOperator() {
    }

    public VdiCreateOperator(int port, String vdi, long size) {
        this.port = port;
        this.vdi = vdi;
        this.size = size;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 5000, context.getCollie(), "vdi", "create", vdi, String.valueOf(size), "-p", String.valueOf(port));
    }
}
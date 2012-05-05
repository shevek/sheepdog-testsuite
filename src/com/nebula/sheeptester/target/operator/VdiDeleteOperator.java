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
public class VdiDeleteOperator extends AbstractProcessOperator {

    private int port;
    private String vdi;

    public VdiDeleteOperator() {
    }

    public VdiDeleteOperator(int port, String vdi) {
        this.port = port;
        this.vdi = vdi;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 2000, context.getCollie(), "vdi", "delete", "-p", String.valueOf(port), vdi);
    }
}

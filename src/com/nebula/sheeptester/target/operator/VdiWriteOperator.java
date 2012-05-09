/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.InputProcess;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.util.GeneratorInputStream;

/**
 *
 * @author shevek
 */
public class VdiWriteOperator extends AbstractProcessOperator {

    private int port;
    private String vdi;
    private long offset;
    private int length;

    public VdiWriteOperator() {
    }

    public VdiWriteOperator(int port, String vdi, long offset, int length) {
        this.port = port;
        this.vdi = vdi;
        this.offset = offset;
        this.length = length;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        GeneratorInputStream input = new GeneratorInputStream(offset, length);
        InputProcess process = new InputProcess(context, input, context.getCollie(), "vdi", "write", "-p", String.valueOf(port), vdi, String.valueOf(offset), String.valueOf(length));
        return process;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.OutputProcess;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.util.GeneratorOutputStream;
import com.nebula.sheeptester.util.RepeatingOutputStream;
import java.io.OutputStream;

/**
 *
 * @author shevek
 */
public class VdiReadOperator extends AbstractProcessOperator {

    private int port;
    private String vdi;
    private long offset;
    private int length;
    private byte[] data;

    public VdiReadOperator() {
    }

    public VdiReadOperator(int port, String vdi, long offset, int length, byte[] data) {
        this.port = port;
        this.vdi = vdi;
        this.offset = offset;
        this.length = length;
        this.data = data;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        OutputStream output;
        if (data == null)
            output = new GeneratorOutputStream(offset, length);
        else
            output = new RepeatingOutputStream(data, offset, length);
        OutputProcess process = new OutputProcess(context, output, context.getCollie(), "vdi", "read", "-p", String.valueOf(port), vdi, String.valueOf(offset), String.valueOf(length));
        return process;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.InputProcess;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.util.GeneratorInputStream;
import com.nebula.sheeptester.util.RepeatingInputStream;
import java.io.InputStream;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author shevek
 */
public class VdiWriteOperator extends AbstractProcessOperator {

    private int port;
    private String vdi;
    private long offset;
    private int length;
    private byte[] data;

    public VdiWriteOperator() {
    }

    public VdiWriteOperator(int port, String vdi, long offset, int length, byte[] data) {
        this.port = port;
        this.vdi = vdi;
        this.offset = offset;
        this.length = length;
        this.data = data;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        InputStream input;
        if (ArrayUtils.isEmpty(data))
            input = new GeneratorInputStream(offset, length);
        else
            input = new RepeatingInputStream(data, offset, length);
        InputProcess process = new InputProcess(context, input, context.getCollie(), "vdi", "write", "-p", String.valueOf(port), vdi, String.valueOf(offset), String.valueOf(length));
        return process;
    }
}

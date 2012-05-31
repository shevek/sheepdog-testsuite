/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.OutputProcess;
import com.nebula.sheeptester.util.GeneratorOutputStream;
import com.nebula.sheeptester.util.RepeatingOutputStream;
import com.nebula.sheeptester.util.ValidatingOutputStream;
import java.io.IOException;

/**
 *
 * @author shevek
 */
public class VdiReadOperator extends AbstractOperator {

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
    public Response run(TargetContext context) throws Exception {
        try {
            ValidatingOutputStream output;
            if (data == null)
                output = new GeneratorOutputStream(offset, length);
            else
                output = new RepeatingOutputStream(data, offset, length);
            OutputProcess process = new OutputProcess(context, output, context.getCollie(), "vdi", "read", "-p", String.valueOf(port), vdi, String.valueOf(offset), String.valueOf(length));
            process.execute();
            if (output.isError())
                throw new TargetException("ValidatingOutputStream flagged an error.");
            return new AbstractProcessOperator.ProcessResponse(this, process);
        } catch (IOException e) {
            throw new TargetException(e);
        }
    }
}
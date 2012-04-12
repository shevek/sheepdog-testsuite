/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author shevek
 */
public class GeneratorOutputStream extends OutputStream {

    private long offset;
    private int length;

    public GeneratorOutputStream(long offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void write(int b) throws IOException {
        if (length <= 0)
            throw new IOException("Unexpected byte.");
        length--;
        if ((byte) b != (byte) offset)
            throw new IOException("Expected " + ((byte) offset) + " but got " + b);
        offset++;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (length != 0)
            throw new IOException("Closed with length " + length);
    }
}

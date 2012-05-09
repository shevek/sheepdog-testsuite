/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class GeneratorOutputStream extends OutputStream {

    private static final Log LOG = LogFactory.getLog(GeneratorOutputStream.class);
    private long offset;
    private int length;
    private boolean error = false;

    public GeneratorOutputStream(long offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void write(int b) throws IOException {
        if (error)
            return;
        if (length == 0) {
            error = true;
            LOG.warn("Unexpected extra byte.");
            return;
        }
        length--;
        try {
            if ((byte) b != (byte) offset) {
                error = true;
                LOG.warn("Expected " + ((byte) offset) + " but got " + b);
                return;
            }
        } finally {
            offset++;
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        LOG.info("Closed.");
        if (length != 0)
            throw new IOException("Closed with length " + length);
    }
}

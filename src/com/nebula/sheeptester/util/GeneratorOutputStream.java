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
public class GeneratorOutputStream extends ValidatingOutputStream {

    private static final Log LOG = LogFactory.getLog(GeneratorOutputStream.class);
    private long offset;
    private int length;

    public GeneratorOutputStream(long offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void write(int b) throws IOException {
        if (isError())
            return;
        if (length == 0) {
            setError(true);
            LOG.warn("Unexpected extra byte.");
            return;
        }
        length--;
        try {
            if ((byte) b != (byte) offset) {
                setError(true);
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
        if (length != 0) {
            setError(true);
            LOG.warn("Closed with length " + length);
        }
    }
}

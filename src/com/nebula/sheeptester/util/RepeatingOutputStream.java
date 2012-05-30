/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class RepeatingOutputStream extends ValidatingOutputStream {

    private static final Log LOG = LogFactory.getLog(RepeatingOutputStream.class);
    private final byte[] data;
    private int offset;
    private int length;

    public RepeatingOutputStream(byte[] data, long offset, int length) {
        this.data = data;
        this.offset = (int) (offset % data.length);
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
            if ((byte) b != data[offset]) {
                setError(true);
                LOG.warn("Expected " + data[offset] + " but got " + b);
                return;
            }
        } finally {
            offset++;
            if (offset >= data.length)
                offset = 0;
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

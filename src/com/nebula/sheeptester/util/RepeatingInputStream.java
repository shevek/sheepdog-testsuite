/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class RepeatingInputStream extends InputStream {

    private static final Log LOG = LogFactory.getLog(RepeatingInputStream.class);
    private byte[] data;
    private int offset;
    private int length;

    public RepeatingInputStream(byte[] data, long offset, int length) {
        this.data = data;
        this.offset = (int) (offset % data.length);
        this.length = length;
    }

    @Override
    public int read() throws IOException {
        if (length <= 0)
            return -1;
        length--;
        byte b = data[offset++];
        if (offset >= data.length)
            offset = 0;
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int remaining = len;
        while (remaining > 0) {
            if (remaining < data.length - offset) {
                System.arraycopy(data, offset, b, off, remaining);
                offset += remaining;
            } else {
                int delta = data.length - offset;
                System.arraycopy(data, offset, b, off, delta);
                remaining -= delta;
                offset = 0;
            }
        }
        return len;
    }

    @Override
    public void close() throws IOException {
        super.close();
        LOG.info("Closed.");
    }
}

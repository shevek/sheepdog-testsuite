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
public class GeneratorInputStream extends InputStream {

    private static final Log LOG = LogFactory.getLog(GeneratorInputStream.class);
    private long offset;
    private int length;

    public GeneratorInputStream(long offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    @Override
    public int read() throws IOException {
        if (length <= 0)
            return -1;
        length--;
        return (int) offset++ & 0xFF;
    }

    @Override
    public void close() throws IOException {
        super.close();
        LOG.info("Closed.");
    }
}

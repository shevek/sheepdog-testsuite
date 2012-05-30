/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class RepeatingOutputStreamTest {

    public static final int LENGTH = 1023;

    @Test
    public void testByte() throws Exception {
        byte[] data = new byte[]{1, 2, 3, 4};
        RepeatingInputStream in = new RepeatingInputStream(data, 0, LENGTH);
        RepeatingOutputStream out = new RepeatingOutputStream(data, 0, LENGTH);
        for (int i = 0; i < 1023; i++)
            out.write(in.read());
        assertFalse(out.isError());
        assertEquals(-1, in.read());
        out.write(0);
        assertTrue(out.isError());
    }

    @Test
    public void testBlock() throws Exception {
        byte[] data = new byte[]{1, 2, 3, 4};
        RepeatingInputStream in = new RepeatingInputStream(data, 0, LENGTH);
        RepeatingOutputStream out = new RepeatingOutputStream(data, 0, LENGTH);
        IOUtils.copy(in, out);
        assertFalse(out.isError());
        assertEquals(-1, in.read());
        out.write(0);
        assertTrue(out.isError());
    }
}

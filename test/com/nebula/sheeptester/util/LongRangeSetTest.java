/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import com.nebula.sheeptester.util.LongRangeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class LongRangeSetTest {

    private static final Log LOG = LogFactory.getLog(LongRangeSetTest.class);

    @Test
    public void testEmpty() {
        LongRangeSet set = new LongRangeSet();
        long value = Double.doubleToLongBits(Math.random());
        assertFalse(set.contains(value));
    }

    @Test
    public void testSimple() {
        LongRangeSet set = new LongRangeSet();
        set.add(4, 8);
        LOG.info("Set is " + set);

        assertFalse(set.contains(0));
        assertFalse(set.contains(1));
        assertFalse(set.contains(2));
        assertFalse(set.contains(3));
        assertTrue(set.contains(4));
        assertTrue(set.contains(5));
        assertTrue(set.contains(6));
        assertTrue(set.contains(7));
        assertFalse(set.contains(8));
        assertFalse(set.contains(9));
        assertFalse(set.contains(10));
    }

    @Test
    public void testComplexII() {
        LongRangeSet set = new LongRangeSet();
        set.add(2, 4);
        set.add(6, 8);
        set.add(10, 14);
        assertEquals(3, set.size());

        set.add(3, 11);
        assertEquals(1, set.size());

        LongRangeSet other = new LongRangeSet();
        other.add(2, 14);
        assertEquals(other, set);
    }

    @Test
    public void testComplexIO() {
        LongRangeSet set = new LongRangeSet();
        set.add(2, 4);
        set.add(6, 8);
        set.add(10, 14);
        assertEquals(3, set.size());

        set.add(3, 9);
        assertEquals(2, set.size());

        LongRangeSet other = new LongRangeSet();
        other.add(2, 9);
        other.add(10, 14);
        assertEquals(other, set);
    }

    @Test
    public void testComplexOI() {
        LongRangeSet set = new LongRangeSet();
        set.add(2, 4);
        set.add(6, 8);
        set.add(10, 14);
        assertEquals(3, set.size());

        set.add(5, 11);
        assertEquals(2, set.size());

        LongRangeSet other = new LongRangeSet();
        other.add(2, 4);
        other.add(5, 14);
        assertEquals(other, set);
    }

    @Test
    public void testComplexOO() {
        LongRangeSet set = new LongRangeSet();
        set.add(2, 4);
        set.add(6, 8);
        set.add(10, 14);
        assertEquals(3, set.size());

        set.add(5, 9);
        assertEquals(3, set.size());

        LongRangeSet other = new LongRangeSet();
        other.add(2, 4);
        other.add(5, 9);
        other.add(10, 14);
        assertEquals(other, set);
    }
}

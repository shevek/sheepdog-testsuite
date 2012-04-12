/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.util.Arrays;
import javax.annotation.Nonnegative;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class LongRangeSet {

    private static final Log LOG = LogFactory.getLog(LongRangeSet.class);

    /* A set of ranges, written as [start, end), [start, end), [start, end), ... */
    private long[] range = ArrayUtils.EMPTY_LONG_ARRAY;

    public boolean contains(long off) {
        int idx = Arrays.binarySearch(range, off);
        return (idx & 1) == 0;
    }

    public void add(@Nonnegative long start, @Nonnegative long end) {
        if (end <= start)
            return;

        int idx_s = Arrays.binarySearch(range, start);
        // LOG.info("idx_s == " + idx_s);
        if (idx_s < 0)
            idx_s = -idx_s - 1;
        // LOG.info("idx_s == " + idx_s);
        if ((idx_s & 1) != 0) {
            idx_s--;
            start = range[idx_s];
        }
        // idx_s is now a pointer to the first start point after 'start'
        assert (idx_s & 1) == 0;
        assert idx_s >= range.length || range[idx_s] >= start;

        int idx_e = Arrays.binarySearch(range, end);
        if (idx_e < 0)
            idx_e = -idx_e - 1;
        if ((idx_e & 1) != 0) {
            end = range[idx_e];
            idx_e++;
        }
        // idx_e is now a pointer to the first start point after 'end'
        assert (idx_e & 1) == 0;
        assert idx_e >= range.length || range[idx_e] >= end;

        // This loop may never iterate.
        int len = range.length; // The number of values non-clobbered.
        for (int i = idx_s; i < idx_e; i++) {
            range[i] = Long.MAX_VALUE;
            len--;
        }

        Arrays.sort(range);
        range = Arrays.copyOf(range, len + 2);
        range[len] = start;
        range[len + 1] = end;
        Arrays.sort(range);
    }

    @Nonnegative
    public int size() {
        return range.length >> 1;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(range);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().equals(obj.getClass()))
            return false;
        LongRangeSet other = (LongRangeSet) obj;
        return Arrays.equals(range, other.range);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        boolean b = false;
        for (int i = 0; i < range.length; i += 2) {
            if (b)
                buf.append(",");
            else
                b = true;
            buf.append("[").append(range[i]).append(",").append(range[i + 1]).append(")");
        }
        buf.append("}");
        return buf.toString();
    }
}

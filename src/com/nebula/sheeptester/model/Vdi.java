/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.model;

import com.nebula.sheeptester.util.LongRangeSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class Vdi {

    private static final Log LOG = LogFactory.getLog(Vdi.class);
    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = 1024 * KILOBYTE;
    private static final long GIGABYTE = MEGABYTE * 1024;
    private static final long BLOCKSIZE = 4 * MEGABYTE;
    private static final Random RANDOM = new Random();

    // Really, these should be longs, but in tests, we never use gigabytes
    private static long newRandom(long limit) {
        if (limit >= Integer.MAX_VALUE || limit < 0)
            throw new IllegalArgumentException("Out of range: " + limit);
        // Cheap unsigned
        synchronized (RANDOM) {
            return RANDOM.nextInt((int) limit);
        }
    }

    @Nonnegative
    public static long newSize() {
        return MEGABYTE + newRandom(200 * MEGABYTE);
    }
    private static final AtomicInteger COUNTER = new AtomicInteger();
    private final String name;
    private final long size;
    private final LongRangeSet ranges = new LongRangeSet();

    public Vdi(@Nonnull String name, long size) {
        this.name = name;
        this.size = size;
    }

    public Vdi() {
        this("_test_" + COUNTER.getAndIncrement(), newSize());
    }

    public String getName() {
        return name;
    }

    @Nonnegative
    public long getSize() {
        return size;
    }

    public void addRange(@Nonnegative long start, @Nonnegative long end) {
        ranges.add(start, end);
    }

    @Nonnegative
    public long newOffset() {
        long blocks = getSize() / BLOCKSIZE;
        long block = newRandom(blocks + 1);
        // LOG.info("Size = " + getSize() + "; offset = " + (block * BLOCKSIZE) + "; block = " + block + "/" + blocks);
        return block * BLOCKSIZE;
    }

    @Nonnegative
    public int newLength(long offset) {
        return (int) Math.min(100 * KILOBYTE, getSize() - offset);
    }

    @Override
    public String toString() {
        return "Vdi(" + getName() + "[" + getSize() + "])";
    }
}

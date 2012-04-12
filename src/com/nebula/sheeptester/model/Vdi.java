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

/**
 *
 * @author shevek
 */
public class Vdi {

    private static final long KILOBYTE = 1024;
    private static final long MEGABYTE = 1024 * KILOBYTE;
    private static final long GIGABYTE = MEGABYTE * 1024;
    private static final Random RANDOM = new Random();

    private static synchronized long newRandom(long limit) {
        return RANDOM.nextLong() % limit;
    }

    public static synchronized long newSize() {
        return MEGABYTE + newRandom(20 * MEGABYTE);
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
        return newRandom(getSize() - 100 * KILOBYTE);
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

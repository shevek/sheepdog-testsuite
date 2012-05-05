/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import org.apache.commons.collections15.CollectionUtils;

/**
 *
 * @author shevek
 */
public class RandomUtils {

    private static final Random RANDOM = new Random();

    public static int nextInt(int max) {
        synchronized (RANDOM) {
            return RANDOM.nextInt(max);
        }
    }

    public static <T> T getRandom(@Nonnull List<T> objects) {
        int idx = nextInt(objects.size());
        return objects.get(idx);
    }

    public static <T> T getRandom(@Nonnull Collection<T> objects) {
        int idx = nextInt(objects.size());
        return (T) CollectionUtils.get(objects, idx);
    }

    public static <T> T getRandom(@Nonnull Map<?, ? extends T> objects) {
        int size = objects.size();
        if (size <= 0)
            throw new IllegalArgumentException("Cannot select a random item from 0 objects.");
        int idx = nextInt(size);
        Map.Entry<?, ? extends T> e = (Map.Entry<?, ? extends T>) CollectionUtils.get(objects, idx);
        return e.getValue();
    }
}

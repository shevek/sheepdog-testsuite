/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.Factory;

/**
 *
 * @author shevek
 */
public class MapFactory<K, V> implements Factory<Map<K, V>> {

    @Override
    public Map<K, V> create() {
        return new HashMap<K, V>();
    }
}

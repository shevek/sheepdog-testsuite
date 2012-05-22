/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections15.Factory;

/**
 *
 * @author shevek
 */
public class ListFactory<T> implements Factory<List<T>> {

    @Override
    public List<T> create() {
        return new ArrayList<T>();
    }
}

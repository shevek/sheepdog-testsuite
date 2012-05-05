/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

/**
 *
 * @author shevek
 */
public class SizeUtils {

    String SUFFIXES = "kMGT";

    public long parseSize(String size) throws NumberFormatException {
        if (size.endsWith("b"))
            size = size.substring(0, size.length() - 1);
        long shift = 1024;
        return 0L;
    }
}

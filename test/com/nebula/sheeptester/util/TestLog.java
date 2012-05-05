/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import org.apache.commons.logging.impl.SimpleLog;

/**
 *
 * @author shevek
 */
public class TestLog extends SimpleLog {

    public TestLog(String name) {
        super(name);
    }

    @Override
    protected void write(StringBuffer buffer) {
        System.out.println(buffer);
    }

    public static void main(String[] args) {
        System.out.println("Ready");
    }
}

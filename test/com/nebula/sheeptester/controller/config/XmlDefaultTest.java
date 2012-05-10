/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.config;

import java.io.StringReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author shevek
 */
public class XmlDefaultTest {

    private static final Log LOG = LogFactory.getLog(XmlDefaultTest.class);

    @Root(name = "foo")
    public static class Foo {

        @Attribute(required = false)
        public int value = 4;
    }

    @Test
    public void testDeserializeDefault() throws Exception {
        StringReader sr = new StringReader("<foo />");
        Serializer s = new Persister();
        Foo f = s.read(Foo.class, sr);
        LOG.info("Value is " + f.value);
    }
}

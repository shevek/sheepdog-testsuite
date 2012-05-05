/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.config;

import java.io.File;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author shevek
 */
public class RootConfigurationTest {

    private static final Log LOG = LogFactory.getLog(RootConfigurationTest.class);

    @Test
    public void testConfigurationReader() throws Exception {
        Serializer serializer = new Persister();
        for (int i = 0; i < 10; i++) {
            String name = "cfg-0" + i + ".xml";
            URL url = getClass().getResource(name);
            if (url == null)
                continue;
            File file = new File(url.toURI());
            LOG.info("Loading from " + url);
            // InputStream in = // getClass().getResourceAsStream(name);
            try {
                RootConfiguration configuration = serializer.read(RootConfiguration.class, file);
                LOG.info("Loaded " + configuration);
            } finally {
                // IOUtils.closeQuietly(in);
            }
        }
    }
}
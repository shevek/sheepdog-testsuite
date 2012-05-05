/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sheep")
public class SheepConfiguration {

    @Attribute(required = false)
    private String id;
    @Attribute
    private String hostId;
    @Attribute
    private int port;
    @Attribute
    private String directory;

    public void init() {
        if (id == null)
            id = hostId + "-" + port;
    }

    public String getId() {
        return id;
    }

    public String getHostId() {
        return hostId;
    }

    public int getPort() {
        return port;
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return getId() + "(" + getHostId() + ":" + getPort() + getDirectory() + ")";
    }
}
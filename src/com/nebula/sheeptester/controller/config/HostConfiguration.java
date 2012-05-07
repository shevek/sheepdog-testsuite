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
@Root(name = "host")
public class HostConfiguration {

    @Attribute(required = false)
    private String id;
    @Attribute
    private String host;
    @Attribute(required = false)
    private int port = 22;
    @Attribute
    private String user;
    @Attribute
    private String password;
    @Attribute(required = false)
    private String sheep;
    @Attribute(required = false)
    private String collie;

    public void init() {
        if (id == null)
            id = host;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public String getSheep() {
        return sheep;
    }

    public String getCollie() {
        return collie;
    }

    public String toStringAddress() {
        return getUser() + /* ":" + getPassword() + */ "@" + getHost() + ":" + getPort();

    }

    @Override
    public String toString() {
        return "HostConfiguration(" + toStringAddress() + ")";
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.config.SheepConfiguration;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class Sheep {

    @Nonnull
    private final Host host;
    private final SheepConfiguration config;
    private int pid = -1;

    public Sheep(@Nonnull Host host, @Nonnull SheepConfiguration config) {
        this.host = host;
        this.config = config;
    }

    @Nonnull
    public ControllerContext getContext() {
        return getHost().getContext();
    }

    @Nonnull
    public Host getHost() {
        return host;
    }

    @Nonnull
    public SheepConfiguration getConfig() {
        return config;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isRunning() {
        return getPid() > 0;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Sheep(");
        buf.append(getHost());
        buf.append(", ");
        buf.append(getConfig());
        buf.append(")=");
        if (isRunning())
            buf.append(getPid());
        else
            buf.append("<not-running>");
        return buf.toString();
    }
}

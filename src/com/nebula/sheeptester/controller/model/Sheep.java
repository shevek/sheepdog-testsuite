/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.config.HostConfiguration;
import com.nebula.sheeptester.controller.config.SheepConfiguration;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class Sheep implements Comparable<Sheep> {

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
    public int compareTo(Sheep o) {
        int cmp = getHost().compareTo(o.getHost());
        if (cmp != 0)
            return cmp;
        return getConfig().getPort() - o.getConfig().getPort();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Sheep(");

        Host h = getHost();
        HostConfiguration hc = h.getConfig();
        SheepConfiguration sc = getConfig();

        // buf.append(hc.getUser()).append('@');
        buf.append(hc.getHost());
        buf.append(':').append(sc.getPort());
        // buf.append(':').append(sc.getDirectory());
        if (isRunning())
            buf.append(";pid=").append(getPid());

        buf.append(")");
        return buf.toString();
    }
}

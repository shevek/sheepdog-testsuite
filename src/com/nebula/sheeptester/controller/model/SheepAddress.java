/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class SheepAddress {

    private final String hostname;
    private final int port;

    public SheepAddress(@Nonnull String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Nonnull
    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SheepAddress other = (SheepAddress) obj;
        if ((this.hostname == null) ? (other.hostname != null) : !this.hostname.equals(other.hostname))
            return false;
        if (this.port != other.port)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.hostname != null ? this.hostname.hashCode() : 0);
        hash = 37 * hash + this.port;
        return hash;
    }

    @Override
    public String toString() {
        return hostname + ":" + port;
    }
}

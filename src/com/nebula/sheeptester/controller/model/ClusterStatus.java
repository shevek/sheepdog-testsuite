/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

/**
 *
 * @author shevek
 */
public enum ClusterStatus {

    RUNNING, WAITING, NEEDSFORMAT, HALTED, UNKNOWN;

    public static ClusterStatus forString(String text) {
        if (text.startsWith("running"))
            return RUNNING;
        if (text.startsWith("Waiting for other nodes"))
            return WAITING;
        if (text.startsWith("Waiting for cluster to be formatted"))
            return NEEDSFORMAT;
        if (text.startsWith("IO has halted as"))
            return HALTED;
        throw new IllegalArgumentException("Unknown status " + text);
    }
}

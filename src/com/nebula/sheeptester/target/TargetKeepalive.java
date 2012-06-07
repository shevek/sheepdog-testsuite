/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class TargetKeepalive extends Thread {

    private static final Log LOG = LogFactory.getLog(TargetKeepalive.class);
    private volatile boolean running = true;

    public TargetKeepalive() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                System.out.println();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOG.warn("Interrupted", e);
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
        interrupt();
    }
}

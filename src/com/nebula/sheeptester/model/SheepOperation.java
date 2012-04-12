/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.model;

import com.nebula.sheeptester.exec.ChannelProcess;
import com.nebula.sheeptester.exec.ChannelProcessAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepOperation extends ChannelProcessAdapter implements Runnable {

    private static final Log LOG = LogFactory.getLog(SheepOperation.class);
    private final SheepOperator operator;
    private final Sheep sheep;
    private final Vdi vdi;

    public SheepOperation(SheepOperator operator, Sheep sheep, Vdi vdi) {
        this.operator = operator;
        this.sheep = sheep;
        this.vdi = vdi;
    }

    protected void init() {
    }

    @Override
    public void run() {
        LOG.info("Executing " + this);
        init();
        try {
            ChannelProcess process = operator.newProcess(sheep, vdi);
            process.addChannelProcessListener(this);
            process.start();
            process.await();
        } catch (InterruptedException e) {
            LOG.error("Failed: " + this, e);
        } finally {
            fini();
        }
    }

    protected void fini() {
    }

    @Override
    public String toString() {
        return sheep + " " + operator + " " + vdi;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

import com.nebula.sheeptester.target.operator.Operator;
import javax.annotation.Nonnull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class TargetOperation implements Runnable {

    private static final Log LOG = LogFactory.getLog(TargetOperation.class);
    private final TargetContext context;
    private final Operator operator;

    public TargetOperation(TargetContext context, Operator operator) {
        this.context = context;
        this.operator = operator;
    }

    @Nonnull
    public Operator getOperator() {
        return operator;
    }

    protected void init() {
    }

    @Override
    public void run() {
        LOG.info("Executing " + this);
        init();
        try {
            Object response = operator.run(context);
        } catch (Exception e) {
            LOG.error("Failed: " + this, e);
        } finally {
            fini();
        }
    }

    protected void fini() {
    }

    @Override
    public String toString() {
        return operator.toString();
    }
}

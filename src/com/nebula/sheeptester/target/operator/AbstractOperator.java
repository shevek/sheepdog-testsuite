/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author shevek
 */
public abstract class AbstractOperator implements Operator {

    private static final AtomicInteger ID = new AtomicInteger(1);
    private transient int id = ID.getAndIncrement();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    protected void toStringBuilderArgs(@Nonnull StringBuilder buf) {
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(ClassUtils.getSimpleName(getClass()));
        buf.append("[");
        buf.append(getId());
        buf.append("]");

        toStringBuilderArgs(buf);

        return buf.toString();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import javax.annotation.Nonnull;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author shevek
 */
public class AbstractResponse implements Response {

    private transient int id;

    public AbstractResponse() {
    }

    public AbstractResponse(@Nonnull Operator operator) {
        this.id = operator.getId();
    }

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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ClassUtils;

/**
 *
 * @author shevek
 */
public interface Operator {

    public int getId();

    public void setId(int id);

    @Nonnull
    public AbstractResponse run(TargetContext context) throws Exception;
}

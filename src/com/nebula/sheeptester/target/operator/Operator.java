/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public interface Operator {

    public int getId();

    public void setId(int id);

    @Nonnull
    public Response run(TargetContext context) throws Exception;
}

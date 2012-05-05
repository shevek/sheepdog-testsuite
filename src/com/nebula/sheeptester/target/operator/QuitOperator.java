/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;

/**
 *
 * @author shevek
 */
public class QuitOperator extends AbstractOperator {

    @Override
    public AbstractResponse run(TargetContext context) throws Exception {
        System.exit(0);
        return new AbstractResponse(this);
    }
}

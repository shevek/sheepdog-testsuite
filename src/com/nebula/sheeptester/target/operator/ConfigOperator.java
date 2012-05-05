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
public class ConfigOperator extends AbstractOperator {

    private String hostId;
    private String sheep;
    private String collie;

    public ConfigOperator() {
    }

    public ConfigOperator(String hostId, String sheep, String collie) {
        this.hostId = hostId;
        this.sheep = sheep;
        this.collie = collie;
    }

    @Override
    public AbstractResponse run(TargetContext context) throws Exception {
        return new DefaultResponse(this);
    }
}

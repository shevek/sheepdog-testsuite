/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.TimedProcess;

/**
 *
 * @author shevek
 */
public class SheepStatOperator extends AbstractOperator {

    public static class StatResponse extends AbstractResponse {

        public StatResponse() {
        }

        public StatResponse(Operator operator) {
            super(operator);
        }
    }
    private int port;

    public SheepStatOperator() {
    }

    public SheepStatOperator(int port) {
        this.port = port;
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        TimedProcess process = new TimedProcess(context, 1000, context.getCollie() + "cluster", "info", "-p", String.valueOf(port));
        process.execute();
        if (process.getError().size() > 0)
            throw new TargetException("cluster info -p " + port + ": stderr: " + process.getError());

        return new StatResponse(this);
    }
}

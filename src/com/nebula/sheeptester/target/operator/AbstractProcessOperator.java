/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.TargetProcess;
import java.io.IOException;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public abstract class AbstractProcessOperator extends AbstractOperator {

    public static class ProcessResponse extends AbstractResponse {

        private String output;
        private String error;

        public ProcessResponse() {
        }

        @Nonnull
        public String getOutput() {
            return output;
        }

        @Nonnull
        public String getError() {
            return error;
        }

        public ProcessResponse(AbstractOperator operator, TargetProcess process) {
            super(operator);
            output = process.getOutput().toString();
            error = process.getError().toString();
        }
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        try {
            TargetProcess process = newProcess(context);
            process.execute();
            return new ProcessResponse(this, process);
        } catch (IOException e) {
            throw new TargetException(e);
        }
    }

    protected abstract TargetProcess newProcess(TargetContext context);
}

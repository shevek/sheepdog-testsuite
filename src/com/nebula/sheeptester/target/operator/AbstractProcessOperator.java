/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.TargetProcess;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public abstract class AbstractProcessOperator extends AbstractOperator {

    public static class ProcessResponse extends AbstractResponse {

        private byte[] output;
        private byte[] error;

        public ProcessResponse() {
        }

        @Nonnull
        public byte[] getOutput() {
            return output;
        }

        @Nonnull
        public String getOutputAsString() {
            try {
                return new String(getOutput(), "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        @Nonnull
        public byte[] getError() {
            return error;
        }

        @Nonnull
        public String getErrorAsString() {
            try {
                return new String(getError(), "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        public ProcessResponse(AbstractOperator operator, TargetProcess process) {
            super(operator);
            output = process.getOutput().toByteArray();
            error = process.getError().toByteArray();
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

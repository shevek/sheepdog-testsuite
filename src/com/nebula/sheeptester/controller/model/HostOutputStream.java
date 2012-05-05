/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import com.nebula.sheeptester.controller.ControllerContext;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public abstract class HostOutputStream extends OutputStream {

    private static final Log LOG = LogFactory.getLog(HostOutputStream.class);
    private final ControllerContext context;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public HostOutputStream(ControllerContext context) {
        this.context = context;
    }

    // Accessed only from the jsch session I/O thread.
    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            try {
                process(buffer.toString());
            } catch (Throwable t) {
                context.addError("Failed while processing incoming data", t);
            } finally {
                buffer.reset();
            }
        } else {
            buffer.write(b);
        }
    }

    protected abstract void process(String line);
}

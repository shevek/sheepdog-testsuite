/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.exec;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nebula.sheeptester.model.ComputeNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.apache.commons.io.input.ClosedInputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class ChannelProcess {

    private static final Log LOG = LogFactory.getLog(ChannelProcess.class);

    protected class SnifferOutputStream extends TeeOutputStream {

        public SnifferOutputStream(OutputStream out, OutputStream branch) {
            super(out, new CloseShieldOutputStream(branch));
        }

        @Override
        public void close() throws IOException {
            super.close();
            done(true);
        }
    }

    public enum State {

        INIT, SUCCESS, ERROR;
    }
    private final ComputeNode node;
    private final String command;
    private final List<ChannelProcessListener> listeners = new CopyOnWriteArrayList<ChannelProcessListener>();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ByteArrayOutputStream error = new ByteArrayOutputStream();
    private final MutableBoolean done = new MutableBoolean(false);
    private ChannelExec channel;

    public ChannelProcess(ComputeNode node, String command) {
        LOG.info(node + ": " + command);
        this.node = node;
        this.command = command;
    }

    @Nonnull
    public ByteArrayOutputStream getOutput() {
        return output;
    }

    @Nonnull
    public ByteArrayOutputStream getError() {
        return error;
    }

    @OverridingMethodsMustInvokeSuper
    protected void init(ChannelExec channel) {
        channel.setCommand(command);
        channel.setInputStream(new ClosedInputStream());
        channel.setOutputStream(new SnifferOutputStream(output, System.out));
        channel.setErrStream(new SnifferOutputStream(error, System.err));
    }

    @CheckForNull
    public ChannelExec start() {
        try {
            Session session = node.getSession();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            init(channel);
            channel.connect();
            this.channel = channel;
            return channel;
        } catch (JSchException e) {
            node.clearSession();
            LOG.error("Start failed", e);
            for (ChannelProcessListener listener : listeners)
                listener.error(this, "Failed to connect", e);
            done(false);
            return null;
        }
    }

    protected void done(boolean success) {
        synchronized (done) {
            if (done.booleanValue())
                return;
            done.setValue(true);
            done.notifyAll();
        }

        ChannelExec c = channel;
        if (c != null) {
            c.disconnect();
            channel = null;
        }

        if (error.size() > 0) {
            String message = "Command: " + command + "\n\tText in standard error: " + error;
            LOG.error(message);
            for (ChannelProcessListener listener : listeners)
                listener.error(this, message, null);
            success = false;
        }
        if (success) {
            for (ChannelProcessListener listener : listeners)
                listener.success(this);
        }
        // LOG.info("Process done.", new Throwable());
        for (ChannelProcessListener listener : listeners)
            listener.done(this);
    }

    public void await() throws InterruptedException {
        synchronized (done) {
            while (!done.booleanValue())
                done.wait();
        }
    }

    public void addChannelProcessListener(ChannelProcessListener listener) {
        synchronized (done) {
            if (done.booleanValue())
                throw new IllegalStateException("Already done.");
        }
        listeners.add(listener);
    }
}
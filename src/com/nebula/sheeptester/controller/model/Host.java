/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.nebula.sheeptester.util.ConsoleUserInfo;
import com.nebula.sheeptester.controller.ControllerContext;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.nebula.sheeptester.controller.ControllerAssertionException;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.config.HostConfiguration;
import com.nebula.sheeptester.target.operator.ExceptionResponse;
import com.nebula.sheeptester.target.operator.Operator;
import com.nebula.sheeptester.target.operator.QuitOperator;
import com.nebula.sheeptester.target.operator.Response;
import com.nebula.sheeptester.util.SimpleFuture;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class Host implements Comparable<Host> {

    private static final Log LOG = LogFactory.getLog(Host.class);
    private final ControllerContext context;
    private final HostConfiguration config;
    // private final Map<Integer, Sheep> sheep = new HashMap<Integer, Sheep>();
    @GuardedBy("lock")
    private ChannelExec channel;
    @GuardedBy("lock")
    private PrintWriter writer;
    @GuardedBy("lock")
    private Map<Integer, SimpleFuture<Response>> requests = new HashMap<Integer, SimpleFuture<Response>>();
    private final Object lock = new Object();

    public Host(ControllerContext context, HostConfiguration config) {
        this.context = context;
        this.config = config;
    }

    @Nonnull
    public ControllerContext getContext() {
        return context;
    }

    @Nonnull
    public HostConfiguration getConfig() {
        return config;
    }

    public void connect() throws IOException {
        synchronized (lock) {
            try {
                Session session = context.getJsch().getSession(config.getUser(), config.getHost());
                session.setUserInfo(new ConsoleUserInfo(config.getPassword()));
                session.setDaemonThread(true);
                session.connect();

                ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                sftp.connect();
                InputStream in = FileUtils.openInputStream(context.getJarFile());
                try {
                    sftp.put(in, "sheeptester.jar");
                } catch (SftpException e) {
                    throw new IOException(e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
                sftp.disconnect();

                ChannelExec exec = (ChannelExec) session.openChannel("exec");
                exec.setPty(false);
                exec.setCommand("ulimit -c unlimited && java -jar sheeptester.jar --target");
                exec.setErrStream(new CloseShieldOutputStream(System.err));
                exec.setOutputStream(new HostOutputStream(context) {

                    final Gson gson = context.getGson();

                    @Override
                    protected void process(String line) {
                        if (context.isVerbose())
                            LOG.info(Host.this + " <<< " + line);
                        Response response = gson.fromJson(line, Response.class);
                        SimpleFuture<Response> future;
                        synchronized (lock) {
                            future = requests.remove(response.getId());
                        }
                        if (future == null)
                            LOG.warn("No future for request " + response.getId());
                        else
                            future.setValue(response);
                    }

                    @Override
                    public void close() throws IOException {
                        synchronized (lock) {
                            for (SimpleFuture<?> f : requests.values())
                                f.setThrowable(new EOFException());
                        }
                    }
                });

                this.channel = exec;
                this.writer = new PrintWriter(channel.getOutputStream(), true);   // Now this is synchronized and flushing.

                exec.connect();
            } catch (JSchException e) {
                throw new IOException(e);
            }
        }
    }

    public void disconnect() {
        synchronized (lock) {
            String text = context.getGson().toJson(new QuitOperator(), Operator.class);
            writer.println(text);
            writer.close();
            channel.disconnect();
            channel = null;
        }
    }

    public Response execute(ControllerContext context, Operator object) throws ControllerException, InterruptedException {
        String text = context.getGson().toJson(object, Operator.class);
        // LOG.info("Run " + this + ": " + text);
        if (context.isVerbose())
            LOG.info(Host.this + " >>> " + text);
        SimpleFuture<Response> future = new SimpleFuture<Response>();
        synchronized (lock) {
            requests.put(object.getId(), future);
            writer.println(text);   // synchronizes internally and flushes
        }
        Response response;
        try {
            response = future.get();
        } catch (ExecutionException e) {
            if (e.getCause() != null)
                throw new ControllerAssertionException("Failed while executing " + object, e.getCause());
            else
                throw new ControllerAssertionException("Failed while executing " + object, e);
        }
        if (response == null)
            throw new NullPointerException("Request did not generate a response: " + object);
        if (response instanceof ExceptionResponse) {
            ExceptionResponse eresponse = (ExceptionResponse) response;
            throw new ControllerAssertionException(eresponse.getMessage());
        }
        return response;
    }

    @Override
    public int compareTo(Host o) {
        return getConfig().getId().compareTo(o.getConfig().getId());
    }

    @Override
    public String toString() {
        return "Host(" + config.toStringAddress() + ")";
    }
}
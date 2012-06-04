/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.exec;

import com.nebula.sheeptester.target.TargetAssertionException;
import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.input.ClosedInputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class TargetProcess {

    private static final Log LOG = LogFactory.getLog(TargetProcess.class);

    protected class SnifferOutputStream extends TeeOutputStream {

        public SnifferOutputStream(OutputStream out, OutputStream branch) {
            super(out, new CloseShieldOutputStream(branch));
        }
    }
    private final TargetContext context;
    private final String[] command;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ByteArrayOutputStream error = new ByteArrayOutputStream();
    private InputStream inputStream = new ClosedInputStream();
    private OutputStream outputStream = output;
    private OutputStream errorStream = error;
    private boolean silent = false;

    public TargetProcess(TargetContext context, String... command) {
        this.context = context;
        this.command = command;
    }

    @Nonnull
    public TargetContext getContext() {
        return context;
    }

    @Nonnull
    public ByteArrayOutputStream getOutput() {
        return output;
    }

    @Nonnull
    public ByteArrayOutputStream getError() {
        return error;
    }

    public void setInputStream(@Nonnull InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(@Nonnull OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setErrorStream(@Nonnull OutputStream errorStream) {
        this.errorStream = errorStream;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    @OverridingMethodsMustInvokeSuper
    protected void init(@Nonnull Executor executor) {
        // LOG.info("InputStream = " + inputStream);
        // LOG.info("OutputStream = " + outputStream);
        // LOG.info("ErrorStream = " + errorStream);
        executor.setStreamHandler(new PumpStreamHandler(outputStream, errorStream, inputStream));
    }

    public int execute() throws IOException, TargetException {
        CommandLine commandline = new CommandLine(command[0]);
        for (int i = 1; i < command.length; i++)
            commandline.addArgument(command[i], false);

        Map<String, String> variables = new HashMap<String, String>();
        variables.putAll(context.getProperties());
        variables.put("SHEEP", context.getSheep());
        variables.put("COLLIE", context.getCollie());
        commandline.setSubstitutionMap(variables);

        if (!isSilent())
            LOG.info(context.getHostId() + ": " + commandline);

        DefaultExecutor executor = new DefaultExecutor();
        init(executor);
        return execute(executor, commandline);
    }

    protected int execute(Executor executor, CommandLine commandline) throws TargetException, IOException {
        try {
            return executor.execute(commandline);
        } catch (ExecuteException e) {
            StringBuilder buf = new StringBuilder();
            buf.append("Execution of ").append(commandline).append(" failed:");
            buf.append("\n\t").append(e).append("\n");

            buf.append("Standard output:");
            if (getOutput().size() > 0)
                buf.append(":\n").append(getOutput()).append("\n");
            else
                buf.append(" (no data on stdout)\n");

            buf.append("Standard error:");
            if (getError().size() > 0)
                buf.append("\n").append(getError());
            else
                buf.append(" (no data on stderr)");

            throw new TargetAssertionException(buf.toString(), e);
        }
    }

    @Override
    public String toString() {
        return context.getHostId() + ": " + Arrays.toString(command);
    }
}
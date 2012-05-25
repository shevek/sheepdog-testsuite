/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.target.operator.AbstractProcessOperator.ProcessResponse;
import com.nebula.sheeptester.target.operator.ExecOperator;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 *
 * @author shevek
 */
@Root(name = "exec")
public class ExecCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(ExecCommand.class);
    @Attribute(required = false)
    private String hostId;
    @Text
    private String script;
    @Attribute(required = false)
    private int timeout = -1;

    @Override
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        List<Host> hosts = toHosts(context, hostId);
        ControllerExecutor executor = context.newExecutor(hosts.size());
        for (final Host host : hosts) {
            executor.submit("Executing script on host " + host, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    ExecCommand.this.run(context, host);
                }
            });
        }
        executor.await();
    }

    private void run(ControllerContext context, Host host) throws ControllerException, InterruptedException {
        String[] lines = StringUtils.split(script, "\r\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 0)
                run(context, host, line);
        }
    }

    private void run(ControllerContext context, Host host, String command) throws ControllerException, InterruptedException {
        CommandLine line = org.apache.commons.exec.CommandLine.parse(command.trim());
        ExecOperator operator = new ExecOperator(timeout, line.toStrings());
        ProcessResponse response = (ProcessResponse) context.execute(host, operator);
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(host).append("] ").append(command);
        if (response.getOutput().length > 0)
            buf.append("\n")/*.append("Output:\n")*/.append(response.getOutputAsString());
        if (response.getError().length > 0)
            buf.append("\nError:\n").append(response.getErrorAsString());
        LOG.info(buf);
    }
}

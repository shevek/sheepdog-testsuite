/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.target.operator.ExecOperator;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 *
 * @author shevek
 */
@Root(name = "exec")
public class ExecCommand extends AbstractCommand {

    @Attribute(required = false)
    private String hostId;
    @Text
    private String script;
    @Attribute(required = false)
    private int timeout = -1;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        if (hostId != null) {
            for (String id : StringUtils.split(hostId, ", ")) {
                Host host = getHost(context, id);
                run(context, host);
            }
        } else {
            Host host = toHost(context, null);
            run(context, host);
        }
    }

    private void run(ControllerContext context, Host host) throws ControllerException, InterruptedException {
        CommandLine line = org.apache.commons.exec.CommandLine.parse(script.trim());
        ExecOperator operator = new ExecOperator(timeout, line.toStrings());
        context.execute(host, operator);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.ExecOperator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "node-list")
public class NodeListCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(NodeListCommand.class);
    @Attribute(required = false)
    private String sheepId;

    @Override
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        List<Sheep> sheeps = toSheeps(context, sheepId);
        run(context, sheeps);
    }

    public static void run(final ControllerContext context, List<? extends Sheep> sheeps) throws ControllerException, InterruptedException {
        ControllerExecutor executor = context.newExecutor(sheeps.size());
        for (final Sheep sheep : sheeps) {
            executor.submit("Getting node list", new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    NodeListCommand.run(context, sheep);
                }
            });
        }
        executor.await();
    }

    public static void run(ControllerContext context, Sheep sheep) throws ControllerException, InterruptedException {
        ExecOperator operator = new ExecOperator(5000, "${COLLIE}", "node", "list", "-p", String.valueOf(sheep.getConfig().getPort()));

        Host host = sheep.getHost();
        context.execute(host, operator);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        if (sheepId != null)
            buf.append(" sheepId=").append(sheepId);
        else
            buf.append(" <all-sheep>");
    }
}

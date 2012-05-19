/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.ExecOperator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "cluster-format")
public class ClusterFormatCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(ClusterFormatCommand.class);
    @Attribute(required = false)
    private String sheepId;
    @Attribute(required = false)
    private int copies;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);
        int _copies = copies;
        if (_copies <= 0)
            _copies = 3;
        run(context, sheep, _copies);
    }

    public static void run(ControllerContext context, Sheep sheep, int _copies) throws ControllerException, InterruptedException {
        ExecOperator operator = new ExecOperator(5000, "${COLLIE}", "cluster", "format", "-c", String.valueOf(_copies), "-p", String.valueOf(sheep.getConfig().getPort()));

        Host host = sheep.getHost();
        context.execute(host, operator);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        if (sheepId != null)
            buf.append(" sheepId=").append(sheepId);
        else
            buf.append(" <any-sheep>");
    }
}

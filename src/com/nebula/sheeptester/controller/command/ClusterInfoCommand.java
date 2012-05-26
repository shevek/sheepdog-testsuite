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
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "cluster-info")
public class ClusterInfoCommand extends AbstractCommand {

    @Attribute(required = false)
    private String sheepId;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);

        ExecOperator operator = new ExecOperator(5000, "${COLLIE}", "cluster", "info", "-p", String.valueOf(sheep.getConfig().getPort()));

        Host host = sheep.getHost();
        context.execute(host, operator);
    }
}

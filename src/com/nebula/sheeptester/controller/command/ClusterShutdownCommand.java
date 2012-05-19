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
@Root(name = "cluster-shutdown")
public class ClusterShutdownCommand extends AbstractCommand {

    @Attribute(required = false)
    private String sheepId;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);
        run(context, sheep);
    }

    public void run(ControllerContext context, Sheep sheep) throws ControllerException, InterruptedException {
        ExecOperator operator = new ExecOperator(5000,
                "${COLLIE}", "cluster", "shutdown", "-p", String.valueOf(sheep.getConfig().getPort()));

        Host host = sheep.getHost();
        context.execute(host, operator);
        for (Sheep s : context.getSheep().values())
            s.setPid(-1);
        Thread.sleep(1000);
    }
}

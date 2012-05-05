/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.SheepKillOperator;
import java.util.concurrent.ExecutionException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sheep-kill")
public class SheepKillCommand extends AbstractCommand {

    @Attribute(required = false)
    private String sheepId;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        if (sheepId != null) {
            Sheep sheep = toSheep(context, sheepId);
            if (!sheep.isRunning())
                throw new IllegalStateException("Attempted to kill sheep which is not running.");
            SheepKillOperator operator = new SheepKillOperator(sheep.getPid());

            Host host = sheep.getHost();
            context.execute(host, operator);
        } else {
            for (Sheep sheep : context.getSheeps().values()) {
                if (!sheep.isRunning())
                    continue;
                SheepKillOperator operator = new SheepKillOperator(sheep.getPid());

                Host host = sheep.getHost();
                context.execute(host, operator);
            }
        }
    }
}

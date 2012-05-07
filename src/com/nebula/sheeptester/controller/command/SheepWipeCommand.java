/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.SheepWipeOperator;
import java.util.concurrent.ExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sheep-wipe")
public class SheepWipeCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(SheepWipeCommand.class);
    @Attribute(required = false)
    private String hostId;
    @Attribute(required = false)
    private String sheepId;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        if (sheepId != null) {
            Sheep sheep = getSheep(context, sheepId);
            run(context, sheep);
        } else if (hostId != null) {
            Host host = getHost(context, hostId);
            for (Sheep sheep : context.getSheep(host).values()) {
                run(context, sheep);
            }
        } else {
            for (Sheep sheep : context.getSheep().values()) {
                run(context, sheep);
            }
        }
        Thread.sleep(2000);
    }

    public static void run(ControllerContext context, Sheep sheep) throws InterruptedException, ExecutionException {
        if (sheep.isRunning())
            SheepKillCommand.run(context, sheep);

        SheepWipeOperator operator = new SheepWipeOperator(sheep.getConfig().getDirectory());

        Host host = sheep.getHost();
        context.execute(host, operator);
    }
}

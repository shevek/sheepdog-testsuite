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
import javax.annotation.Nonnull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sheep-kill")
public class SheepKillCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(SheepKillCommand.class);
    @Attribute(required = false)
    private String hostId;
    @Attribute(required = false)
    private String sheepId;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        if (sheepId != null) {
            Sheep sheep = getSheep(context, sheepId);
            if (!sheep.isRunning()) {
                LOG.warn("Attempted to kill sheep which is not running: " + sheep);
                return;
            }
            run(context, sheep);
        } else if (hostId != null) {
            Host host = getHost(context, hostId);
            run(context, host);
        } else {
            for (Host host : context.getHosts()) {
                run(context, host);
            }
        }
        Thread.sleep(200);
    }

    public static void run(@Nonnull ControllerContext context, @Nonnull Sheep sheep) throws InterruptedException, ExecutionException {
        if (!sheep.isRunning())
            return;

        if (sheep.getPid() == Integer.MAX_VALUE) {
            LOG.warn("Cannot kill sheep with unknown pid " + sheep);
            return;
        }
        SheepKillOperator operator = new SheepKillOperator(sheep.getPid());

        Host host = sheep.getHost();
        context.execute(host, operator);
        sheep.setPid(-1);
    }

    public static void run(@Nonnull ControllerContext context, @Nonnull Host host) throws InterruptedException, ExecutionException {
        SheepKillOperator operator = new SheepKillOperator(-1);

        context.execute(host, operator);
        for (Sheep sheep : context.getSheep(host).values())
            sheep.setPid(-1);
    }
}

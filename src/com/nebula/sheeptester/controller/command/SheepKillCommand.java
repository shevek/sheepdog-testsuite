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
import com.nebula.sheeptester.target.operator.SheepKillOperator;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
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
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        if (sheepId != null) {
            List<Sheep> sheeps = toSheeps(context, sheepId);
            run_sheeps(context, sheeps);
        } else if (hostId != null) {
            Host host = getHost(context, hostId);
            run(context, host);
        } else {
            Collection<? extends Host> hosts = context.getHosts();
            run(context, hosts);
        }
        Thread.sleep(200);
    }

    public static void run_sheeps(@Nonnull final ControllerContext context, @Nonnull Collection<? extends Sheep> sheeps) throws ControllerException, InterruptedException {
        ControllerExecutor executor = context.newExecutor(sheeps.size());
        for (final Sheep sheep : sheeps) {
            executor.submit("Killing sheep " + sheep, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    SheepKillCommand.run(context, sheep);
                }
            });
        }
        executor.await();
    }

    public static void run(@Nonnull ControllerContext context, @Nonnull Sheep sheep) throws ControllerException, InterruptedException {
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

    public static void run(@Nonnull final ControllerContext context, @Nonnull Collection<? extends Host> hosts) throws ControllerException, InterruptedException {
        ControllerExecutor executor = context.newExecutor(hosts.size());
        for (final Host host : hosts) {
            executor.submit("Killing all sheep on " + host, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    SheepKillCommand.run(context, host);
                }
            });
        }
        executor.await();
    }

    public static void run(@Nonnull ControllerContext context, @Nonnull Host host) throws ControllerException, InterruptedException {
        SheepKillOperator operator = new SheepKillOperator(-1);

        context.execute(host, operator);
        for (Sheep sheep : context.getSheep(host).values())
            sheep.setPid(-1);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        if (sheepId != null)
            buf.append(" sheepId=").append(sheepId);
        if (hostId != null)
            buf.append(" hostId=").append(hostId);
        if (sheepId == null && hostId == null)
            buf.append(" <all-sheep>");
    }
}

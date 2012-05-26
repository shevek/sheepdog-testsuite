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
import com.nebula.sheeptester.target.operator.SheepWipeOperator;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
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
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        if (sheepId != null) {
            List<Sheep> sheeps = toSheeps(context, sheepId, null);
            SheepKillCommand.run_sheeps(context, sheeps);
            run(context, sheeps);
        } else if (hostId != null) {
            Host host = getHost(context, hostId);
            SheepKillCommand.run(context, host);
            for (Sheep sheep : context.getSheep(host).values()) {
                run(context, sheep);
            }
        } else {
            SheepKillCommand.run(context, context.getHosts());
            Collection<? extends Sheep> sheeps = context.getSheep().values();
            ControllerExecutor executor = context.newExecutor(sheeps.size());
            for (final Sheep sheep : sheeps) {
                executor.submit("Wiping sheep " + sheep, new ControllerExecutor.Task() {

                    @Override
                    public void run() throws Exception {
                        SheepWipeCommand.run(context, sheep);
                    }
                });
            }
            executor.await();
        }
        Thread.sleep(2000);
    }

    public static void run(@Nonnull final ControllerContext context, @Nonnull Collection<? extends Sheep> sheeps) throws ControllerException, InterruptedException {
        ControllerExecutor executor = context.newExecutor(sheeps.size());
        for (final Sheep sheep : sheeps) {
            executor.submit("Wiping sheep " + sheep, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    SheepWipeCommand.run(context, sheep);
                }
            });
        }
        executor.await();
    }

    public static void run(ControllerContext context, Sheep sheep) throws ControllerException, InterruptedException {
        SheepWipeOperator operator = new SheepWipeOperator(sheep.getConfig().getDirectory());

        Host host = sheep.getHost();
        context.execute(host, operator);
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

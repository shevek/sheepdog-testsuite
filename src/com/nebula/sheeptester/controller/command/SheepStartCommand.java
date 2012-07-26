/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import com.nebula.sheeptester.controller.config.HostConfiguration;
import com.nebula.sheeptester.controller.config.SheepConfiguration;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.AbstractProcessOperator.ProcessResponse;
import com.nebula.sheeptester.target.operator.Response;
import com.nebula.sheeptester.target.operator.SheepStartOperator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sheep-start")
public class SheepStartCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(SheepStartCommand.class);
    @Attribute(required = false)
    public String hostId;
    @Attribute(required = false)
    public String sheepId;
    @Attribute(required = false)
    public String cluster;
    @Attribute(required = false)
    public int vnodes = -1;
    @Attribute(required = false)
    public int zone = -1;
    @Attribute(required = false)
    public int delay;
    @Attribute(required = false)
    public int predelay;
    @Attribute(required = false)
    public int interdelay;
    @Attribute(required = false)
    public int postdelay;
    @Attribute(required = false)
    public boolean strace = false;
    @Attribute(required = false)
    public boolean valgrind = false;

    private int getPreDelay() {
        if (predelay > 0)
            return predelay;
        if (delay > 0)
            return delay;
        return 0;
    }

    private int getInterDelay() {
        if (interdelay > 0)
            return interdelay;
        if (delay > 0)
            return delay;
        return 0;
    }

    private int getPostDelay(ControllerContext context) {
        if (postdelay > 0)
            return postdelay;
        if (delay > 0)
            return delay;
        if (valgrind)
XXX            return isZooKeeper(context) ? 10000 : 5000;
XXX        if (isZooKeeper(context))
            return 1000;
        return 200;
    }

XXX    public boolean isZooKeeper(ControllerContext context) {
XXX       if (StringUtils.contains(cluster, "zookeeper"))
XXX            return true;
XXX        if (cluster != null)
XXX            return false;
XXX        for (HostConfiguration config : context.getConfiguration().getHosts())
XXX            if (StringUtils.contains(config.getCluster(), "zookeeper"))
XXX                return true;
XXX        return false;
XXX    }

    private void sleep(int delay, String reason) throws InterruptedException {
        if (delay > 0) {
            LOG.info("Sleeping for " + delay + " ms: " + reason);
            Thread.sleep(delay);
        }
    }

    public void preSleep() throws InterruptedException {
        sleep(getPreDelay(), "Pre-start delay.");
    }

    public void interSleep() throws InterruptedException {
        sleep(getInterDelay(), "Inter-start delay.");
    }

    public void postSleep(ControllerContext context) throws InterruptedException {
        sleep(getPostDelay(context), "Post-start delay.");
    }

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        List<Sheep> sheeps = new ArrayList<Sheep>();
        if (sheepId != null) {
            for (Sheep sheep : toSheeps(context, sheepId, null)) {
                if (sheep.isRunning())
                    continue;
                sheeps.add(sheep);
            }
        } else if (hostId != null) {
            for (Host host : toHosts(context, hostId)) {
                for (Sheep sheep : context.getSheep(host).values()) {
                    if (sheep.isRunning())
                        continue;
                    sheeps.add(sheep);
                }
            }
        } else {
            for (Sheep sheep : context.getSheep().values()) {
                if (sheep.isRunning())
                    continue;
                sheeps.add(sheep);
            }
        }

        preSleep();

        Set<Host> hosts = new HashSet<Host>();
        try {
            boolean parallel = getInterDelay() <= 0;
            for (Sheep sheep : sheeps) {
                hosts.add(sheep.getHost());
                if (!parallel) {
                    run(context, sheep);
                    interSleep();
                }
            }
            if (parallel)
                run(context, sheeps);
        } finally {
            postSleep(context);
            SheepStatCommand stat = new SheepStatCommand();
            stat.statHosts(context, hosts, true);
        }
    }

    public void run(final ControllerContext context, Collection<? extends Sheep> sheeps) throws ControllerException, InterruptedException {
        final ControllerExecutor executor = context.newExecutor(sheeps.size());
        for (final Sheep sheep : sheeps) {
            executor.submit("Starting sheep " + sheep, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    SheepStartCommand.this.run(context, sheep);
                }
            });
        }
        executor.await();
    }

    public void run(ControllerContext context, Sheep sheep) throws ControllerException, InterruptedException {
        SheepConfiguration config = sheep.getConfig();
        SheepStartOperator operator = new SheepStartOperator(config.getPort(), config.getDirectory());
        if (cluster != null)
            operator.cluster = cluster;
        else
            operator.cluster = context.getProperty("CLUSTER");
        operator.vnodes = vnodes;
        operator.zone = zone;
        operator.strace = strace;
        operator.valgrind = valgrind;

        Host host = sheep.getHost();
        Response response = context.execute(host, operator);
        if (response instanceof ProcessResponse) {
            ProcessResponse presponse = (ProcessResponse) response;
            if (!ArrayUtils.isEmpty(presponse.getOutput()))
                LOG.info("Output was\n" + presponse.getOutputAsString());
            if (!ArrayUtils.isEmpty(presponse.getError()))
                LOG.info("Error was\n" + presponse.getOutputAsString());
        }

        sheep.setPid(Integer.MAX_VALUE);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        if (sheepId != null)
            buf.append(" sheepId=").append(sheepId);
        else
            buf.append(" <all-sheep>");
        if (vnodes >= 0)
            buf.append(" vnodes=").append(vnodes);
        if (strace)
            buf.append(" <strace>");
        if (valgrind)
            buf.append(" <valgrind>");
    }
}

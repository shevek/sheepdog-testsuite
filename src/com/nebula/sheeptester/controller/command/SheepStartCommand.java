/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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
    private String hostId;
    @Attribute(required = false)
    private String sheepId;
    @Attribute(required = false)
    private boolean parallel;
    @Attribute(required = false)
    private boolean strace;
    @Attribute(required = false)
    private boolean valgrind;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        Set<Host> hosts = new HashSet<Host>();
        try {
            if (sheepId != null) {
                Sheep sheep = getSheep(context, sheepId);
                if (sheep.isRunning()) {
                    LOG.warn("Sheep already running: " + sheep);
                    return;
                }
                hosts.add(sheep.getHost());
                run(context, sheep);
            } else if (hostId != null) {
                List<Sheep> sheeps = new ArrayList<Sheep>();
                Host host = getHost(context, hostId);
                for (Sheep sheep : context.getSheep(host).values()) {
                    if (sheep.isRunning())
                        continue;
                    hosts.add(sheep.getHost());
                    if (parallel)
                        sheeps.add(sheep);
                    else
                        SheepStartCommand.this.run(context, sheep);
                }
                if (parallel)
                    run(context, sheeps);
            } else {
                List<Sheep> sheeps = new ArrayList<Sheep>();
                for (Sheep sheep : context.getSheep().values()) {
                    if (sheep.isRunning())
                        continue;
                    hosts.add(sheep.getHost());
                    if (parallel)
                        sheeps.add(sheep);
                    else
                        run(context, sheep);
                }
                if (parallel)
                    run(context, sheeps);
            }
        } finally {
            if (valgrind)
                Thread.sleep(2000);
            else
                Thread.sleep(200);
            SheepStatCommand.run(context, hosts);
        }
    }

    public void run(final ControllerContext context, Collection<? extends Sheep> sheeps) throws InterruptedException, ExecutionException {
        final CountDownLatch latch = new CountDownLatch(sheeps.size());
        final ExecutorService executor = context.getExecutor();
        for (final Sheep sheep : sheeps) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        SheepStartCommand.this.run(context, sheep);
                    } catch (Exception e) {
                        context.addError("Failed while starting " + sheep, e);
                    } finally {
                        latch.countDown();
                    }
                }
            };
            executor.submit(runnable);
        }
        latch.await();
    }

    public void run(ControllerContext context, Sheep sheep) throws InterruptedException, ExecutionException {
        SheepConfiguration config = sheep.getConfig();
        SheepStartOperator operator = new SheepStartOperator(config.getPort(), config.getDirectory());
        operator.strace = strace;
        operator.valgrind = valgrind;

        Host host = sheep.getHost();
        Response response = context.execute(host, operator);
        if (response instanceof ProcessResponse) {
            ProcessResponse presponse = (ProcessResponse) response;
            if (!StringUtils.isBlank(presponse.getOutput()))
                LOG.info("Output was\n" + presponse.getOutput());
            if (!StringUtils.isBlank(presponse.getError()))
                LOG.info("Error was\n" + presponse.getOutput());
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
    }
}

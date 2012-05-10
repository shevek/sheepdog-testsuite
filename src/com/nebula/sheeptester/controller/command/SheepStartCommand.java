/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
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
    private int vnodes = -1;
    @Attribute(required = false)
    private boolean parallel;
    @Attribute(required = false)
    private boolean strace;
    @Attribute(required = false)
    private boolean valgrind;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        List<Sheep> sheeps = new ArrayList<Sheep>();
        if (sheepId != null) {
            for (String id : StringUtils.split(sheepId, ", ")) {
                Sheep sheep = getSheep(context, id);
                if (sheep.isRunning()) {
                    LOG.warn("Sheep already running: " + sheep);
                    continue;
                }
                sheeps.add(sheep);
            }
        } else if (hostId != null) {
            for (String id : StringUtils.split(hostId, ", ")) {
                Host host = getHost(context, id);
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

        Set<Host> hosts = new HashSet<Host>();
        try {
            for (Sheep sheep : sheeps) {
                hosts.add(sheep.getHost());
                if (!parallel)
                    run(context, sheep);
            }
            if (parallel)
                run(context, sheeps);
        } finally {
            if (valgrind)
                Thread.sleep(4000);
            else
                Thread.sleep(200);
            SheepStatCommand.run(context, hosts, true);
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
        operator.vnodes = vnodes;
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
        if (vnodes >= 0)
            buf.append(" vnodes=").append(vnodes);
        if (strace)
            buf.append(" <strace>");
        if (valgrind)
            buf.append(" <valgrind>");
    }
}

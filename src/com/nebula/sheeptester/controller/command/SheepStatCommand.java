/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.SheepListOperator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Root(name = "sheep-stat")
public class SheepStatCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(SheepStatCommand.class);
    @Attribute(required = false)
    private boolean check;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        Collection<? extends Host> hosts = context.getHosts();
        run(context, hosts, check);
        List<String> texts = new ArrayList<String>();
        for (Sheep sheep : context.getSheep().values()) {
            if (sheep.isRunning())
                texts.add(sheep.toString());
        }
        Collections.sort(texts);
        for (String text : texts) {
            LOG.info(text);
        }
    }

    public static void run(final ControllerContext context, Collection<? extends Host> hosts, final boolean check) throws InterruptedException, ExecutionException {
        final CountDownLatch latch = new CountDownLatch(hosts.size());
        final ExecutorService executor = context.getExecutor();
        for (final Host host : hosts) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        SheepStatCommand.run(context, host, check);
                    } catch (Exception e) {
                        context.addError("Failed while executing on " + host, e);
                    } finally {
                        latch.countDown();
                    }
                }
            };
            executor.submit(runnable);
        }
        latch.await();
    }

    public static void run(ControllerContext context, Host host, boolean check) throws InterruptedException, ExecutionException {
        List<String> failed = new ArrayList<String>();
        Map<Integer, Sheep> hostSheepMap = new HashMap<Integer, Sheep>();
        for (Sheep sheep : context.getSheep(host).values()) {
            hostSheepMap.put(sheep.getConfig().getPort(), sheep);
        }

        SheepListOperator.SheepListResponse response = (SheepListOperator.SheepListResponse) context.execute(host, new SheepListOperator());
        for (SheepListOperator.SheepProcess process : response.getSheeps()) {
            Sheep sheep = hostSheepMap.remove(process.port);
            if (sheep == null) {
                LOG.warn(host.getConfig().getId() + ": Found an unexpected sheep listening on port " + process.port);
                continue;
            }
            boolean running = sheep.isRunning();
            sheep.setPid(process.pid);
            if (check)
                if (!running)
                    failed.add("Unexpectedly running: " + sheep);
        }
        for (Sheep sheep : hostSheepMap.values()) {
            if (check)
                if (sheep.isRunning())
                    failed.add("Unexpectedly died: " + sheep);
            sheep.setPid(-1);
        }
        if (!failed.isEmpty())
            throw new ExecutionException("Failure in sheep assertions:\n" + StringUtils.join(failed, "\n"), null);
    }
}

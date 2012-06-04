/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerAssertionException;
import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import com.nebula.sheeptester.controller.model.ClusterEpoch;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.AbstractProcessOperator.ProcessResponse;
import com.nebula.sheeptester.target.operator.ExecOperator;
import com.nebula.sheeptester.target.operator.SheepListOperator;
import com.nebula.sheeptester.util.CollieParser;
import com.nebula.sheeptester.controller.model.ClusterInfo;
import com.nebula.sheeptester.controller.model.SheepAddress;
import com.nebula.sheeptester.target.operator.SheepStatOperator;
import com.nebula.sheeptester.target.operator.SheepStatOperator.StatResponse;
import com.nebula.sheeptester.util.ListFactory;
import com.nebula.sheeptester.util.MapFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.CheckForNull;
import org.apache.commons.collections15.map.LazyMap;
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

    public static enum Checks {

        RUNNING, EPOCH;
    }
    @Attribute(required = false)
    private boolean check = true;
    @Attribute(required = false)
    private String status;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Collection<? extends Host> hosts = context.getHosts();
        statHosts(context, hosts, check);

        List<String> texts = new ArrayList<String>();
        for (Sheep sheep : context.getSheep().values()) {
            if (sheep.isRunning())
                texts.add("Sheep is running: " + sheep);
        }
        Collections.sort(texts);
        for (String text : texts) {
            LOG.info(text);
        }

        statSheeps(context, context.getSheep().values());
    }

    public void statHosts(final ControllerContext context, Collection<? extends Host> hosts, final boolean check) throws ControllerException, InterruptedException {
        final ControllerExecutor executor = context.newExecutor(hosts.size());
        for (final Host host : hosts) {
            executor.submit("Stat on " + host, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    statHost(context, host, check);
                }
            });
        }
        executor.await();
    }

    public void statHost(ControllerContext context, Host host, boolean check) throws ControllerException, InterruptedException {
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
            throw new ControllerAssertionException("Failure in sheep assertions:\n" + StringUtils.join(failed, "\n"));
    }

    public Map<Sheep, ClusterInfo> statSheeps(final ControllerContext context, Collection<? extends Sheep> sheeps) throws ControllerException, InterruptedException {
        ControllerExecutor executor = context.newExecutor(sheeps.size());
        final Map<Sheep, ClusterInfo> out = new ConcurrentHashMap<Sheep, ClusterInfo>();
        for (final Sheep sheep : sheeps) {
            executor.submit("Stat sheep " + sheep, new ControllerExecutor.Task() {

                @Override
                public void run() throws Exception {
                    ClusterInfo clusterInfo = statSheep(context, sheep);
                    if (clusterInfo != null)
                        out.put(sheep, clusterInfo);
                }
            });
        }
        executor.await();

        // LOG.info("Sheep: " + out);

        EPOCH:
        {
            Map<Integer, List<Sheep>> epochs = new HashMap<Integer, List<Sheep>>();
            epochs = LazyMap.decorate(epochs, new ListFactory<Sheep>());
            for (Map.Entry<Sheep, ClusterInfo> e : out.entrySet()) {
                epochs.get(e.getValue().getEpoch()).add(e.getKey());
            }
            // LOG.info("Epochs: " + epochs);
            switch (epochs.size()) {
                case 0:
                    LOG.warn("No sheep!");
                    break;
                case 1:
                    LOG.info("All sheep have epoch " + epochs.keySet().iterator().next());
                    break;
                default:
                    throw new ControllerAssertionException("Epoch mismatch: " + epochs);
            }
        }

        MEMBERS:
        {
            Map<Integer, Map<Set<SheepAddress>, List<Sheep>>> members = new TreeMap<Integer, Map<Set<SheepAddress>, List<Sheep>>>();
            members = LazyMap.decorate(members, new MapFactory<Set<SheepAddress>, List<Sheep>>() {

                @Override
                public Map<Set<SheepAddress>, List<Sheep>> create() {
                    Map<Set<SheepAddress>, List<Sheep>> out = new HashMap<Set<SheepAddress>, List<Sheep>>();
                    return LazyMap.decorate(out, new ListFactory<Sheep>());
                }
            });

            for (Map.Entry<Sheep, ClusterInfo> e : out.entrySet()) {
                for (ClusterEpoch epoch : e.getValue().epochs) {
                    members.get(epoch.getId()).get(new TreeSet<SheepAddress>(epoch)).add(e.getKey());
                }
            }

            StringBuilder buf = new StringBuilder();
            for (Map.Entry<Integer, Map<Set<SheepAddress>, List<Sheep>>> e : members.entrySet()) {
                if (e.getValue().size() != 1) {
                    buf.append("Membership mismatch at epoch ").append(e.getKey()).append(":\n");
                    for (Map.Entry<Set<SheepAddress>, List<Sheep>> f : e.getValue().entrySet()) {
                        buf.append("\t").append(f.getKey()).append(": ").append(f.getValue()).append("\n");
                    }
                }
            }
            if (buf.length() > 0) {
                throw new ControllerAssertionException(buf.substring(0, buf.length() - 1));
            }
        }

        return out;
    }

    @CheckForNull
    public ClusterInfo statSheep(ControllerContext context, Sheep sheep) throws ControllerException, InterruptedException {
        try {
            Host host = sheep.getHost();
            StatResponse stat = (StatResponse) context.execute(host, new SheepStatOperator(sheep.getConfig().getDirectory()));
            if (stat.hasCore)
                throw new ControllerAssertionException("Sheep " + sheep + " generated a core file.");
            if (stat.hasLeaks)
                throw new ControllerAssertionException("Sheep " + sheep + " leaked memory.");

            if (!sheep.isRunning())
                return null;
            ExecOperator operator = new ExecOperator(5000, "${COLLIE}", "cluster", "info", "-p", String.valueOf(sheep.getConfig().getPort()));

            ProcessResponse response = (ProcessResponse) context.execute(host, operator);
            ClusterInfo clusterInfo = CollieParser.parseClusterInfo(response.getOutput());
            if (status != null) {
                if (!status.equals(clusterInfo.status.name()))
                    throw new ControllerAssertionException("Sheep " + sheep + " had bad status: Expected " + status + " but got " + clusterInfo.status);
            }

            return clusterInfo;
        } catch (IOException e) {
            throw new ControllerException(e);
        }
    }
}

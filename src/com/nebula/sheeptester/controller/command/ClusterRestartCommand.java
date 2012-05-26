/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.controller.model.Vdi;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
@Root(name = "cluster-restart")
public class ClusterRestartCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(ClusterRestartCommand.class);
    @Attribute
    private String pattern;
    @Attribute
    private int copies;
    @Attribute(required = false)
    private String hostId;
    @Attribute(required = false)
    private String sheepId;
    @Attribute(required = false)
    private String backend;
    @Attribute(required = false)
    private String cluster;
    @Attribute(required = false)
    private String vdiName = "test-vdi";
    @Attribute(required = false)
    private long vdiSize = 100 * 1024;
    @Attribute(required = false)
    private boolean valgrind = false;

    private void addSheep(List<Sheep> out, Collection<? extends Sheep> in) {
        Set<Sheep> set = new HashSet<Sheep>(in);
        set.removeAll(out);
        List<Sheep> list = new ArrayList<Sheep>(set);
        Collections.sort(list);
        int length = Math.max(0, pattern.length() - out.size());
        out.addAll(list.subList(0, length));
    }

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        List<Sheep> sheeps = new ArrayList<Sheep>();
        if (sheepId != null) {
            for (String id : StringUtils.split(sheepId, ", ")) {
                Sheep sheep = getSheep(context, id);
                sheeps.add(sheep);
            }
        }

        if (sheeps.size() < pattern.length()) {
            if (hostId != null) {
                Host host = getHost(context, hostId);
                addSheep(sheeps, context.getSheep(host).values());
            }
        }

        if (sheeps.size() < pattern.length()) {
            addSheep(sheeps, context.getSheep().values());
        }

        WIPE:
        {
            SheepWipeCommand wipe = new SheepWipeCommand();
            wipe.run(context);
        }

        SheepStartCommand start = new SheepStartCommand();
        start.cluster = cluster;
        start.valgrind = valgrind;
        START:
        {
            Role role = Role.NONE;
            for (int i = 0; i < pattern.length(); i++) {
                Sheep sheep = sheeps.get(i);
                start.zone = sheep.getConfig().getPort();
                switch (pattern.charAt(i)) {
                    case 'R':
                    case 'W':
                    case 'X':
                        start.run(context, sheep);
                        role = role.next();
                        break;
                    case 'N':
                        break;
                    case '_':
                        Thread.sleep(1000);
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal pattern character in " + pattern);
                }
                if (role == Role.MASTER)
                    Thread.sleep(200);
            }
        }
        Thread.sleep(300);

        if (start.isZooKeeper(context)) {
            LOG.info("Sleeping to wait for ZooKeeper sessions to create.");
            Thread.sleep(10000);
        }

        FORMAT:
        {
            ClusterFormatCommand.run(context, sheeps.get(0), backend, copies);
        }
        // Thread.sleep(300);

        if (start.isZooKeeper(context)) {
            LOG.info("Sleeping to wait for ZooKeeper sessions to update.");
            Thread.sleep(10000);
        }

        Vdi vdi;
        WRITE:
        {
            VdiCreateCommand create = new VdiCreateCommand();
            vdi = create.run(context, sheeps.get(0), vdiName, vdiSize * 1024);
            VdiWriteCommand write = new VdiWriteCommand();
            write.run(context, sheeps.get(0), vdi);
        }

        SHUTDOWN:
        {
            ClusterShutdownCommand shutdown = new ClusterShutdownCommand();
            shutdown.run(context, sheeps.get(0));
        }

        if (StringUtils.contains(cluster, "zookeeper")) {
            LOG.info("Sleeping to wait for ZooKeeper sessions to expire.");
            Thread.sleep(40000);
        }

        RESTART:
        {
            Role role = Role.NONE;
            for (int i = 0; i < pattern.length(); i++) {
                Sheep sheep = sheeps.get(i);
                start.zone = sheep.getConfig().getPort();
                switch (pattern.charAt(i)) {
                    case 'R':
                        start.run(context, sheep);
                        role = role.next();
                        break;
                    case 'W':
                        SheepWipeCommand.run(context, sheep);
                        start.run(context, sheep);
                        role = role.next();
                        break;
                    case 'X':
                        break;
                    case 'N':
                        start.run(context, sheep);
                        role = role.next();
                        break;
                    case '_':
                        Thread.sleep(1000);
                        break;
                    default:
                        throw new IllegalArgumentException("Illegal pattern character in " + pattern);
                }
                if (role == Role.MASTER)
                    Thread.sleep(200);
            }
        }
    }
}

enum Role {

    NONE() {

        @Override
        public Role next() {
            return MASTER;
        }
    },
    MASTER() {

        @Override
        public Role next() {
            return SLAVE;
        }
    },
    SLAVE() {

        @Override
        public Role next() {
            return SLAVE;
        }
    };

    public abstract Role next();
}
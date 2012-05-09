/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.controller.model.Vdi;
import com.nebula.sheeptester.util.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public abstract class AbstractCommand implements Command {

    protected Host getHost(@Nonnull ControllerContext context, @Nonnull String id) {
        Host host = context.getHost(id);
        if (host == null)
            throw new NullPointerException("Failed to find a host id " + id);
        return host;
    }

    @Nonnull
    protected Host toHost(@Nonnull ControllerContext context, @CheckForNull String id) {
        if (id != null)
            return getHost(context, id);
        List<Host> hosts = new ArrayList<Host>();
        for (Host host : context.getHosts()) {
            hosts.add(host);
        }
        if (hosts.isEmpty())
            throw new IllegalStateException("Cannot select a random running host: No hosts are available.");
        return RandomUtils.getRandom(hosts);
    }

    protected Sheep getSheep(@Nonnull ControllerContext context, @Nonnull String id) {
        Sheep sheep = context.getSheep(id);
        if (sheep == null)
            throw new NullPointerException("Failed to find a sheep id " + id);
        return sheep;
    }

    @Nonnull
    protected Sheep toSheep(@Nonnull ControllerContext context, @CheckForNull String id) {
        if (id != null)
            return getSheep(context, id);
        List<Sheep> sheeps = new ArrayList<Sheep>();
        for (Sheep sheep : context.getSheep().values()) {
            if (sheep.isRunning())
                sheeps.add(sheep);
        }
        if (sheeps.isEmpty())
            throw new IllegalStateException("Cannot select a random running sheep: No sheep are running.");
        return RandomUtils.getRandom(sheeps);
    }

    protected Vdi getVdi(@Nonnull ControllerContext context, @Nonnull String id) {
        Vdi vdi = context.getVdi(id);
        if (vdi == null)
            throw new NullPointerException("Failed to find a vdi id " + id);
        return vdi;
    }

    @Nonnull
    protected Vdi toVdi(@Nonnull ControllerContext context, @CheckForNull String id) {
        if (id != null)
            return getVdi(context, id);
        return RandomUtils.getRandom(context.getVdis());
    }

    public void toStringBuilderIndent(StringBuilder buf, int depth) {
        buf.append("    ");
        for (int i = 0; i < depth; i++)
            buf.append("    ");
    }

    public void toStringBuilderArgs(StringBuilder buf) {
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        toStringBuilderIndent(buf, depth);
        buf.append(getClass().getSimpleName());
        toStringBuilderArgs(buf);
        buf.append("\n");
    }
}

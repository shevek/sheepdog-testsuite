/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.config.SheepConfiguration;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class Sheep {

    @Nonnull
    private final Host host;
    private final SheepConfiguration config;
    private int pid = -1;

    public Sheep(@Nonnull Host host, @Nonnull SheepConfiguration config) {
        this.host = host;
        this.config = config;
    }

    @Nonnull
    public ControllerContext getContext() {
        return getHost().getContext();
    }

    @Nonnull
    public Host getHost() {
        return host;
    }

    @Nonnull
    public SheepConfiguration getConfig() {
        return config;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isRunning() {
        return getPid() > 0;
    }

    /*
    @Nonnull
    public TimedProcess listNodes() {
    TimedProcess process = new TimedProcess(node, getCollie() + " node list -p " + getPort(), 1000);
    return process;
    }
    
    @Nonnull
    public TimedProcess listVdi() {
    TimedProcess process = new TimedProcess(node, getCollie() + " vdi list -p " + getPort(), 1000);
    return process;
    }
    
    @Nonnull
    public TimedProcess createVdi(Vdi vdi) {
    TimedProcess process = new TimedProcess(node, getCollie() + " vdi create " + vdi.getName() + " " + vdi.getSize() + " -p " + getPort(), 5000);
    return process;
    }
    
    @Nonnull
    public InputProcess readVdi(Vdi vdi) {
    long offset = vdi.newOffset();
    int length = vdi.newLength(offset);
    GeneratorInputStream input = new GeneratorInputStream(offset, length);
    InputProcess process = new InputProcess(node, getCollie() + " vdi read -p " + getPort() + " " + vdi.getName() + " " + offset + " " + length, input);
    return process;
    }
    
    @Nonnull
    public OutputProcess writeVdi(final Vdi vdi) {
    final long offset = vdi.newOffset();
    final int length = vdi.newLength(offset);
    GeneratorOutputStream output = new GeneratorOutputStream(offset, length);
    OutputProcess process = new OutputProcess(node, getCollie() + " vdi write -p " + getPort() + " " + vdi.getName() + " " + offset + " " + length, output);
    process.addChannelProcessListener(new TargetProcessAdapter() {
    
    @Override
    public void success(TargetProcess process) {
    vdi.addRange(offset, offset + length);
    }
    });
    return process;
    }
    
    @Nonnull
    public TimedProcess deleteVdi(final Vdi vdi) {
    TimedProcess process = new TimedProcess(node, getCollie() + " vdi delete -p " + getPort() + " " + vdi.getName(), 2000);
    process.addChannelProcessListener(new TargetProcessAdapter() {
    
    @Override
    public void done(TargetProcess process) {
    getContext().removeVdi(vdi);
    }
    });
    return process;
    }
    
    @Nonnull
    public TimedProcess kill() {
    TimedProcess process = new TimedProcess(node, "kill -9 " + pid, 100);
    return process;
    }
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Sheep(");
        buf.append(getHost());
        buf.append(", ");
        buf.append(getConfig());
        buf.append(")");
        return buf.toString();
    }
}

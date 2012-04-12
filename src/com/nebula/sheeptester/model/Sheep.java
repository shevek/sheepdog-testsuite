/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.model;

import com.nebula.sheeptester.util.GeneratorInputStream;
import com.nebula.sheeptester.util.GeneratorOutputStream;
import com.nebula.sheeptester.exec.ChannelProcess;
import com.nebula.sheeptester.exec.ChannelProcessAdapter;
import com.nebula.sheeptester.exec.TimedProcess;
import com.nebula.sheeptester.exec.OutputProcess;
import com.nebula.sheeptester.exec.InputProcess;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class Sheep {

    @Nonnull
    private final ComputeNode node;
    private final int port;
    private final int pid;

    public Sheep(@Nonnull ComputeNode node, @Nonnegative int port, @Nonnegative int pid) {
        this.node = node;
        this.port = port;
        this.pid = pid;
    }

    @Nonnull
    public Context getContext() {
        return getNode().getContext();
    }

    @Nonnull
    public ComputeNode getNode() {
        return node;
    }

    @Nonnegative
    public int getPort() {
        return port;
    }

    @Nonnegative
    public int getPid() {
        return pid;
    }

    @Nonnull
    public TimedProcess listNodes() {
        TimedProcess process = new TimedProcess(node, node.getCollie() + " node list -p " + getPort(), 1000);
        return process;
    }

    @Nonnull
    public TimedProcess listVdi() {
        TimedProcess process = new TimedProcess(node, node.getCollie() + " vdi list -p " + getPort(), 1000);
        return process;
    }

    @Nonnull
    public TimedProcess createVdi(Vdi vdi) {
        TimedProcess process = new TimedProcess(node, node.getCollie() + " vdi create " + vdi.getName() + " " + vdi.getSize() + " -p " + getPort(), 5000);
        return process;
    }

    @Nonnull
    public InputProcess readVdi(Vdi vdi) {
        long offset = vdi.newOffset();
        int length = vdi.newLength(offset);
        GeneratorInputStream input = new GeneratorInputStream(offset, length);
        InputProcess process = new InputProcess(node, node.getCollie() + " vdi read -p " + getPort() + " " + vdi.getName() + " " + offset + " " + length, input);
        return process;
    }

    @Nonnull
    public OutputProcess writeVdi(final Vdi vdi) {
        final long offset = vdi.newOffset();
        final int length = vdi.newLength(offset);
        GeneratorOutputStream output = new GeneratorOutputStream(offset, length);
        OutputProcess process = new OutputProcess(node, node.getCollie() + " vdi write -p " + getPort() + " " + vdi.getName() + " " + offset + " " + length, output);
        process.addChannelProcessListener(new ChannelProcessAdapter() {

            @Override
            public void success(ChannelProcess process) {
                vdi.addRange(offset, offset + length);
            }
        });
        return process;
    }

    @Nonnull
    public TimedProcess deleteVdi(final Vdi vdi) {
        TimedProcess process = new TimedProcess(node, node.getCollie() + " vdi delete -p " + getPort() + " " + vdi.getName(), 2000);
        process.addChannelProcessListener(new ChannelProcessAdapter() {

            @Override
            public void done(ChannelProcess process) {
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

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Sheep(");
        buf.append(node.getUser()).append("@").append(node.getHost());
        buf.append(", port=").append(getPort());
        buf.append(", pid=").append(getPid());
        buf.append(")");
        return buf.toString();
    }
}

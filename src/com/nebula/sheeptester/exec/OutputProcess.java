/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.exec;

import com.jcraft.jsch.ChannelExec;
import com.nebula.sheeptester.model.ComputeNode;
import com.nebula.sheeptester.ValidationException;
import java.io.OutputStream;

/**
 *
 * @author shevek
 */
public class OutputProcess extends ChannelProcess {

    private final OutputStream output;

    public OutputProcess(ComputeNode node, String command, OutputStream output) {
        super(node, command);
        this.output = output;
    }

    @Override
    protected void init(ChannelExec channel) {
        super.init(channel);
        channel.setOutputStream(output);
    }
}

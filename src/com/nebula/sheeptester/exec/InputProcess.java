/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.exec;

import com.jcraft.jsch.ChannelExec;
import com.nebula.sheeptester.model.ComputeNode;
import com.nebula.sheeptester.ValidationException;
import java.io.InputStream;

/**
 *
 * @author shevek
 */
public class InputProcess extends ChannelProcess {

    private final InputStream input;

    public InputProcess(ComputeNode node, String command, InputStream input) {
        super(node, command);
        this.input = input;
    }

    @Override
    protected void init(ChannelExec channel) {
        super.init(channel);
        channel.setInputStream(input);
    }
}

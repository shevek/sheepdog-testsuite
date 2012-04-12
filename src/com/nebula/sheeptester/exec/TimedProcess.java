/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.exec;

import com.jcraft.jsch.ChannelExec;
import com.nebula.sheeptester.model.ComputeNode;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class TimedProcess extends ChannelProcess {

    private static final Log LOG = LogFactory.getLog(TimedProcess.class);
    private static final Timer TIMER = new Timer("TimedProcess Killer", true);
    private final long msecs;

    public TimedProcess(ComputeNode node, String command, long msecs) {
        super(node, command);
        this.msecs = msecs;
    }

    @Override
    public ChannelExec start() {
        final ChannelExec channel = super.start();
        if (channel == null)
            return null;
        TIMER.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (!channel.isConnected())
                        return;
                    LOG.error("Process took too long: " + this);
                    channel.disconnect();
                } catch (Throwable t) {
                    LOG.error("Failed to handle expired process", t);
                } finally {
                    done(false);
                }
            }
        }, msecs);
        return channel;
    }
}

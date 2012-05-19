/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sleep")
public class SleepCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(SleepCommand.class);
    @Attribute
    private long msecs;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        LOG.info("Sleeping for " + msecs + " ms.");
        Thread.sleep(msecs);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        buf.append(" msecs=").append(msecs);
    }
}

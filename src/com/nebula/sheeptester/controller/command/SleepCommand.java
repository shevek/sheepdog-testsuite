/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "sleep")
public class SleepCommand extends AbstractCommand {

    @Attribute
    private long msecs;

    @Override
    public void run(ControllerContext context) {
        try {
            Thread.sleep(msecs);
        } catch (InterruptedException e) {
            context.addError("Sleep interrupted", e);
        }
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        buf.append(" msecs=").append(msecs);
    }
}

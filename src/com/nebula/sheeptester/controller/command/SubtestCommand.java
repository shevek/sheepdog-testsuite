/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.config.RootConfiguration;
import com.nebula.sheeptester.controller.config.TestConfiguration;
import javax.annotation.Nonnull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "subtest")
public class SubtestCommand extends AbstractCommand {

    @Attribute
    private String testId;

    @Nonnull
    private TestConfiguration getTest(ControllerContext context) {
        RootConfiguration configuration = context.getConfiguration();
        TestConfiguration test = configuration.getTest(testId);
        if (test == null)
            throw new NullPointerException("No such test " + testId);
        return test;
    }

    @Override
    public void check(ControllerContext context) throws ControllerException {
        getTest(context);
    }

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        getTest(context).run(context);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        buf.append(" testId=").append(testId);
    }
}

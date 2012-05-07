/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.config.RootConfiguration;
import com.nebula.sheeptester.controller.config.TestConfiguration;
import java.util.concurrent.ExecutionException;
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

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        RootConfiguration configuration = context.getConfiguration();
        TestConfiguration test = configuration.getTest(testId);
        test.run(context);
    }
}

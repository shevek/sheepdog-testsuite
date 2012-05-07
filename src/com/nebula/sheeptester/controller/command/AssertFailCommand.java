/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import java.util.concurrent.ExecutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "assert-fail")
public class AssertFailCommand extends SequentialCommand {

    private static final Log LOG = LogFactory.getLog(AssertFailCommand.class);

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        try {
            super.run(context);
        } catch (Throwable t) {
            LOG.info("Execution failed (as desired): " + t);
            return;
        }
        // Execution succeeded, but did not.
        throw new ExecutionException("Expected execution to fail (erroneously succeeded) in " + this, null);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerAssertionException;
import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "assert-fail")
public class AssertFailCommand extends SequentialCommand {

    private static final Log LOG = LogFactory.getLog(AssertFailCommand.class);
    @Attribute(required = false)
    private String message;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        try {
            super.run(context);
        } catch (ControllerAssertionException t) {
            if (message != null) {
                Pattern pattern = Pattern.compile(message);
                if (!pattern.matcher(t.getMessage()).find())
                    throw new ControllerAssertionException(
                            "Execution failed; expected '" + pattern
                            + "' but got " + t.getMessage());
            }
            LOG.info("Execution failed (as desired):\n" + t.getMessage());
            LOG.info("Continuing...");
            return;
        }
        // Execution succeeded, but did not.
        throw new ControllerAssertionException("Expected execution to fail (erroneously succeeded) in:\n" + this);
    }
}
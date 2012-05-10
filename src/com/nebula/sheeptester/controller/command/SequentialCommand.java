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
@Root(name = "parallel")
public class SequentialCommand extends AbstractMultiCommand {

    private static final Log LOG = LogFactory.getLog(SequentialCommand.class);
    @Attribute(required = false)
    private int repeat;

    @Override
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        for (int i = 0; i < repeat; i++)
            for (Command command : getCommands())
                run(context, command);
    }
}

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
@Root(name = "sequential")
public class SequentialCommand extends AbstractMultiCommand {

    private static final Log LOG = LogFactory.getLog(SequentialCommand.class);
    @Attribute(required = false)
    private int repeat = 1;

    @Override
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        int _repeat = repeat;
        if (_repeat <= 0)
            _repeat = 1;

        for (int i = 0; i < _repeat; i++)
            for (Command command : getCommands())
                run(context, command);
    }
}

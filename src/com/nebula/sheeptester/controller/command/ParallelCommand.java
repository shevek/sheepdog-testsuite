/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.ControllerExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "parallel")
public class ParallelCommand extends AbstractMultiCommand {

    private static final Log LOG = LogFactory.getLog(ParallelCommand.class);
    @Attribute(required = false)
    private int repeat = 1;

    @Override
    public void run(final ControllerContext context) throws ControllerException, InterruptedException {
        int _repeat = repeat;
        if (_repeat <= 0)
            _repeat = 1;

        int total = getCommands().size() * _repeat;
        ControllerExecutor executor = context.newExecutor(total);
        for (int i = 0; i < _repeat; i++) {
            for (final Command command : getCommands()) {
                executor.submit("Executing sub-command " + command, new ControllerExecutor.Task() {

                    @Override
                    public void run() throws Exception {
                        ParallelCommand.this.run(context, command);
                    }
                });
            }
        }
        executor.await();
    }
}

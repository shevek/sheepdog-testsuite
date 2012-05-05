/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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
    private int repeat;

    @Override
    public void run(final ControllerContext context) {
        int _repeat = repeat;
        if (_repeat <= 0)
            _repeat = 1;

        try {
            int total = getCommands().size() * _repeat;
            LOG.info("Waiting for " + total + " commands.");
            final CountDownLatch latch = new CountDownLatch(total);
            final ExecutorService executor = context.getExecutor();
            for (int i = 0; i < _repeat; i++) {
                for (final Command command : getCommands()) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                command.run(context);
                            } catch (Throwable t) {
                                context.addError("Failed while running " + command, t);
                            } finally {
                                LOG.info("Countdown.");
                                latch.countDown();
                            }
                        }
                    };
                    executor.submit(runnable);
                }
            }
            latch.await();
        } catch (InterruptedException e) {
            context.addError("<parallel> was interrupted.", e);
        }
    }
}

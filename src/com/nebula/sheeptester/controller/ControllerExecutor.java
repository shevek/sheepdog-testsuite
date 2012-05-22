/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class ControllerExecutor {

    private static final Log LOG = LogFactory.getLog(ControllerExecutor.class);

    public interface Task {

        public void run() throws Exception;
    }
    private final ControllerContext context;
    private final CountDownLatch latch;
    private int count;
    private final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();

    public ControllerExecutor(ControllerContext context, int count) {
        this.context = context;
        this.latch = new CountDownLatch(count);
        this.count = count;
    }

    public void submit(final String message, final Task task) {
        if (count <= 0)
            throw new IllegalStateException("Already submitted enough tasks for latch.");
        context.getExecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    task.run();
                } catch (Throwable t) {
                    if (!throwable.compareAndSet(null, t))
                        LOG.error("Additional failure: " + message + ": " + t.getMessage());
                } finally {
                    latch.countDown();
                }
            }
        });
        count--;
    }

    public void await() throws ControllerException, InterruptedException {
        if (count != 0)
            throw new IllegalStateException("Not submitted enough tasks.");
        latch.await();
        Throwable t = throwable.get();
        if (t == null)
            return;
        if (t instanceof ControllerException)
            throw (ControllerException) t;
        throw new ControllerException(t);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.config;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.command.AbstractMultiCommand;
import com.nebula.sheeptester.controller.command.Command;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "test")
public class TestConfiguration extends AbstractMultiCommand {

    private static final Log LOG = LogFactory.getLog(TestConfiguration.class);
    private static final AtomicInteger COUNTER = new AtomicInteger();
    @Attribute(required = false)
    private String id;
    @Attribute(required = false)
    private boolean skip;

    @Nonnull
    public String getId() {
        if (id == null)
            id = "_test_" + COUNTER.getAndIncrement();
        return id;
    }

    public boolean isSkip() {
        return skip;
    }

    @Override
    public void run(@Nonnull ControllerContext context) throws InterruptedException, ExecutionException {
        LOG.info("Executing test:\n" + this);
        for (Command command : getCommands())
            command.run(context);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getId()).append(":\n");
        toStringBuilder(buf, 0);
        return buf.toString();
    }
}
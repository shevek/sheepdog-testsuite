/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

/**
 *
 * @author shevek
 */
public abstract class AbstractMultiCommand extends AbstractCommand {

    @ElementListUnion({
    
        @ElementList(inline = true, type = AssertFailCommand.class, required = false),
        @ElementList(inline = true, type = ClusterFormatCommand.class, required = false),
        @ElementList(inline = true, type = ClusterInfoCommand.class, required = false),
        @ElementList(inline = true, type = ClusterRecoverCommand.class, required = false),
        @ElementList(inline = true, type = ClusterRestartCommand.class, required = false),
        @ElementList(inline = true, type = ClusterShutdownCommand.class, required = false),
        @ElementList(inline = true, type = EchoCommand.class, required = false),
        @ElementList(inline = true, type = ExecCommand.class, required = false),
        @ElementList(inline = true, type = NodeInfoCommand.class, required = false),
        @ElementList(inline = true, type = NodeListCommand.class, required = false),
        @ElementList(inline = true, type = ParallelCommand.class, required = false),
        @ElementList(inline = true, type = SheepKillCommand.class, required = false),
        @ElementList(inline = true, type = SheepStartCommand.class, required = false),
        @ElementList(inline = true, type = SheepStatCommand.class, required = false),
        @ElementList(inline = true, type = SheepWipeCommand.class, required = false),
        @ElementList(inline = true, type = SleepCommand.class, required = false),
        @ElementList(inline = true, type = SubtestCommand.class, required = false),
        @ElementList(inline = true, type = VdiCreateCommand.class, required = false),
        @ElementList(inline = true, type = VdiDeleteCommand.class, required = false),
        @ElementList(inline = true, type = VdiListCommand.class, required = false),
        @ElementList(inline = true, type = VdiReadCommand.class, required = false),
        @ElementList(inline = true, type = VdiWriteCommand.class, required = false),

    })
    private List<Command> commands;

    public List<Command> getCommands() {
        if (commands == null)
            return Collections.emptyList();
        return commands;
    }

    protected void run(@Nonnull ControllerContext context, @Nonnull Command command) throws ControllerException, InterruptedException {
        command.run(context);
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        for (Command command : getCommands()) {
            command.toStringBuilder(buf, depth + 1);
        }
    }
}

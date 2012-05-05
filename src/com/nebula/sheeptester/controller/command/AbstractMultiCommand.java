/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import java.util.Collections;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

/**
 *
 * @author shevek
 */
public abstract class AbstractMultiCommand extends AbstractCommand {

    @ElementListUnion({

        @ElementList(inline = true, type = EchoCommand.class, required = false),
        @ElementList(inline = true, type = ParallelCommand.class, required = false),
        @ElementList(inline = true, type = SheepKillCommand.class, required = false),
        @ElementList(inline = true, type = SheepStartCommand.class, required = false),
        @ElementList(inline = true, type = SleepCommand.class, required = false),
        @ElementList(inline = true, type = VdiCreateCommand.class, required = false),

    })
    private List<Command> commands;

    public List<Command> getCommands() {
        if (commands == null)
            return Collections.emptyList();
        return commands;
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        super.toStringBuilder(buf, depth);
        for (Command command : getCommands()) {
            command.toStringBuilder(buf, depth + 1);
        }
    }
}

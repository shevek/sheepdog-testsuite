/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.controller.model.Vdi;
import com.nebula.sheeptester.util.RandomUtils;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public abstract class AbstractCommand implements Command {

    @Nonnull
    protected Sheep toSheep(@Nonnull ControllerContext context, @CheckForNull String name) {
        if (name == null) {
            Sheep sheep = RandomUtils.getRandom(context.getSheeps());
            return sheep;
        } else {
            Sheep sheep = context.getSheep(name);
            if (sheep == null)
                throw new NullPointerException("Failed to find a sheep named " + name);
            return sheep;
        }
    }

    @Nonnull
    protected Vdi toVdi(@Nonnull ControllerContext context, @CheckForNull String name) {
        if (name == null) {
            Vdi sheep = RandomUtils.getRandom(context.getVdis());
            return sheep;
        } else {
            Vdi sheep = context.getVdi(name);
            if (sheep == null)
                throw new NullPointerException("Failed to find a sheep named " + name);
            return sheep;
        }
    }

    public void toStringBuilderIndent(StringBuilder buf, int depth) {
        buf.append("    ");
        for (int i = 0; i < depth; i++)
            buf.append("    ");
    }

    public void toStringBuilderArgs(StringBuilder buf) {
    }

    @Override
    public void toStringBuilder(StringBuilder buf, int depth) {
        toStringBuilderIndent(buf, depth);
        buf.append(getClass().getSimpleName());
        toStringBuilderArgs(buf);
        buf.append("\n");
    }
}

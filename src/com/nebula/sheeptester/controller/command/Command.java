/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public interface Command {

    public void run(@Nonnull ControllerContext context) throws ControllerException, InterruptedException;

    public void toStringBuilder(@Nonnull StringBuilder buf, @Nonnegative int depth);
}

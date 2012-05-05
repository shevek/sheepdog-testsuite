/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TimedProcess;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "cluster-info")
public class ClusterInfoCommand extends AbstractCommand {

    @Override
    public void run(ControllerContext context) {
        throw new UnsupportedOperationException();
    }

    public void run(TargetContext context) {
        // TimedProcess process = new TimedProcess(context, "${COLLIE}", "cluster", "info", "-p", port, 1000);
        // process.start();
    }
}

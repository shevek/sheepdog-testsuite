/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "echo")
public class EchoCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(EchoCommand.class);
    @Attribute
    private String message;

    @Override
    public void run(ControllerContext context) {
        System.out.println(message);
    }
}

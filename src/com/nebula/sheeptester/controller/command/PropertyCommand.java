/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "property")
public class PropertyCommand extends AbstractCommand {

    @Attribute
    private String name;
    @Attribute(required = false)
    private String value;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        context.setProperty(name, value);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.controller.model.Vdi;
import com.nebula.sheeptester.target.operator.ExecOperator;
import javax.annotation.CheckForNull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "vdi-create")
public class VdiCreateCommand extends AbstractCommand {

    @CheckForNull
    @Attribute(required = false)
    private String sheepId;
    @CheckForNull
    @Attribute(required = false)
    private String name;
    @Attribute(required = false)
    private long size;
    @Attribute(required = false)
    private boolean write;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);
        String _name = name;
        if (_name == null)
            _name = Vdi.newName();
        long _size = size;
        if (_size <= 0)
            _size = Vdi.newSize();
        else
            _size = _size * 1024;
        run(context, sheep, _name, _size);
    }

    public Vdi run(ControllerContext context, Sheep sheep, String _name, long _size) throws ControllerException, InterruptedException {
        Host host = sheep.getHost();
        ExecOperator operator = new ExecOperator(5000, "${COLLIE}", "vdi", "create", "-p", String.valueOf(sheep.getConfig().getPort()), _name, String.valueOf(_size));
        context.execute(host, operator);
        Vdi vdi = new Vdi(_name, _size);
        context.addVdi(vdi);
        if (write)
            VdiWriteCommand.run(context, sheep, vdi, 0, (int) _size);
        return vdi;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.controller.model.Vdi;
import com.nebula.sheeptester.target.operator.VdiCreateOperator;
import java.util.concurrent.ExecutionException;
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

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        Sheep sheep = toSheep(context, sheepId);
        String _name = name;
        if (_name == null)
            _name = Vdi.newName();
        long _size = size;
        if (_size <= 0)
            _size = Vdi.newSize();
        else
            _size = _size * 1024;

        Host host = sheep.getHost();
        VdiCreateOperator request = new VdiCreateOperator(sheep.getConfig().getPort(), _name, _size);
        context.execute(host, request);
        Vdi vdi = new Vdi(_name, _size);
        context.addVdi(vdi);
    }
}

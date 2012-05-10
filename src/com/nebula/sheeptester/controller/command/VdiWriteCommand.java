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
import com.nebula.sheeptester.target.operator.VdiWriteOperator;
import javax.annotation.CheckForNull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "vdi-write")
public class VdiWriteCommand extends AbstractCommand {

    @CheckForNull
    @Attribute(required = false)
    private String sheepId;
    @CheckForNull
    @Attribute(required = false)
    private String name;
    @Attribute(required = false)
    private long offset;
    @Attribute(required = false)
    private int length;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);
        Vdi vdi = toVdi(context, name);
        long _offset = offset;
        if (_offset < 0)
            _offset = vdi.newOffset();
        else
            _offset = _offset * 1024;
        int _length = length;
        if (_length <= 0)
            _length = vdi.newLength(_offset);
        else
            _length = _length * 1024;

        Host host = sheep.getHost();
        VdiWriteOperator request = new VdiWriteOperator(sheep.getConfig().getPort(), vdi.getName(), _offset, _length);
        context.execute(host, request);
    }
}

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
import com.nebula.sheeptester.util.EscapeUtils;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
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
    private long offset = 0;
    @Attribute(required = false)
    private int length = -1;
    @Attribute(required = false)
    private boolean random;
    @Attribute(required = false)
    private String pattern;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);
        Vdi vdi = toVdi(context, name);

        long _offset;
        if (random)
            _offset = vdi.newOffset();
        else
            _offset = offset * 1024;

        int _length;
        if (length <= 0)
            _length = (int) vdi.getSize();
        else if (random)
            _length = vdi.newLength(_offset);
        else
            _length = length * 1024;

        run(context, sheep, vdi, _offset, _length);
    }

    public void run(@Nonnull ControllerContext context, @Nonnull Sheep sheep, @Nonnull Vdi vdi) throws ControllerException, InterruptedException {
        run(context, sheep, vdi, 0L, (int) vdi.getSize());
    }

    public void run(@Nonnull ControllerContext context, @Nonnull Sheep sheep, @Nonnull Vdi vdi, long offset, int length) throws ControllerException, InterruptedException {
        byte[] data = null;
        if (pattern != null)
            data = EscapeUtils.unescape_perl_string(pattern);
        Host host = sheep.getHost();
        VdiWriteOperator request = new VdiWriteOperator(sheep.getConfig().getPort(), vdi.getName(), offset, length, data);
        context.execute(host, request);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.controller.model.Vdi;
import com.nebula.sheeptester.target.operator.VdiDeleteOperator;
import java.util.concurrent.ExecutionException;
import javax.annotation.CheckForNull;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "vdi-delete")
public class VdiDeleteCommand extends AbstractCommand {

    @CheckForNull
    @Attribute(required = false)
    private String sheepId;
    @CheckForNull
    @Attribute(required = false)
    private String name;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        Sheep sheep = toSheep(context, sheepId);
        Vdi vdi = toVdi(context, name);
        VdiDeleteOperator request = new VdiDeleteOperator(sheep.getConfig().getPort(), vdi.getName());

        Host host = sheep.getHost();
        context.execute(host, request);
    }
}

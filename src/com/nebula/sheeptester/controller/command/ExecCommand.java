/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.model.Host;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

/**
 *
 * @author shevek
 */
public class ExecCommand extends AbstractCommand {

    @Attribute(required = false)
    private String hostId;
    @Text
    private String script;

    @Override
    public void run(ControllerContext context) throws InterruptedException, ExecutionException {
        if (hostId != null) {
            for (String id : StringUtils.split(hostId, ", ")) {
                Host host = getHost(context, id);
                run(context, host);
            }
        } else {
            Host host = toHost(context, null);
            run(context, host);
        }
    }

    private void run(ControllerContext context, Host host) {
        throw new UnsupportedOperationException();
    }
}

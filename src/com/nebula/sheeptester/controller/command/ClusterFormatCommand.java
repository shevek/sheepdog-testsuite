/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.command;

import com.nebula.sheeptester.controller.ControllerContext;
import com.nebula.sheeptester.controller.ControllerException;
import com.nebula.sheeptester.controller.model.Host;
import com.nebula.sheeptester.controller.model.Sheep;
import com.nebula.sheeptester.target.operator.ExecOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author shevek
 */
@Root(name = "cluster-format")
public class ClusterFormatCommand extends AbstractCommand {

    private static final Log LOG = LogFactory.getLog(ClusterFormatCommand.class);
    public static final String DEFAULT_BACKEND = "farm";
    @Attribute(required = false)
    private String sheepId;
    @Attribute(required = false)
    private String backend;
    @Attribute(required = false)
    private int copies;

    @Override
    public void run(ControllerContext context) throws ControllerException, InterruptedException {
        Sheep sheep = toSheep(context, sheepId);
        int _copies = copies;
        if (_copies <= 0)
            _copies = 3;
        run(context, sheep, backend, _copies);
    }

    public static void run(@Nonnull ControllerContext context, @Nonnull Sheep sheep, @CheckForNull String backend, int copies) throws ControllerException, InterruptedException {
        List<String> command = new ArrayList<String>(Arrays.asList("${COLLIE}", "cluster", "format"));
        if (backend != null)
            command.addAll(Arrays.asList("-b", backend));
        else
            command.addAll(Arrays.asList("-b", DEFAULT_BACKEND));
        command.addAll(Arrays.asList("-c", String.valueOf(copies), "-p", String.valueOf(sheep.getConfig().getPort())));
        ExecOperator operator = new ExecOperator(5000, command.toArray(ArrayUtils.EMPTY_STRING_ARRAY));

        Host host = sheep.getHost();
        context.execute(host, operator);
    }

    @Override
    public void toStringBuilderArgs(StringBuilder buf) {
        super.toStringBuilderArgs(buf);
        if (sheepId != null)
            buf.append(" sheepId=").append(sheepId);
        else
            buf.append(" <any-sheep>");
    }
}

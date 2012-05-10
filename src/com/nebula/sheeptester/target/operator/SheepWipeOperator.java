/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class SheepWipeOperator extends AbstractProcessOperator {

    private static final Log LOG = LogFactory.getLog(SheepWipeOperator.class);
    private String directory;

    public SheepWipeOperator() {
    }

    public SheepWipeOperator(String directory) {
        this.directory = directory;
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            // LOG.warn(context.getHostId() + ": Directory not found: " + dir);
            return new DefaultResponse(this);
        }
        File log = new File(dir, "sheep.log");
        if (!log.isFile()) {
            // LOG.warn(context.getHostId() + ": Log file not found: " + log);
            return new DefaultResponse(this);
        }

        return super.run(context);
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        return new TimedProcess(context, 500, "sudo", "rm", "-rf", directory);
    }
}
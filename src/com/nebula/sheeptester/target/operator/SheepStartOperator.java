/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.BackgroundProcess;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.File;

/**
 *
 * @author shevek
 */
public class SheepStartOperator extends AbstractProcessOperator {

    private int port;
    private String directory;
    public boolean strace;
    public boolean valgrind;

    public SheepStartOperator() {
    }

    public SheepStartOperator(int port, String directory) {
        this.port = port;
        this.directory = directory;
    }

    @Override
    public Response run(TargetContext context) throws Exception {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            TimedProcess process = new TimedProcess(context, 500, "sudo", "mkdir", "-pm", "755", directory);
            process.execute();
        }

        return super.run(context);
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        if (strace)
            return new BackgroundProcess(context, "sudo", "strace", "-f", "-o", directory + "/strace.out", context.getSheep(), "-l7", "-d", "-p", String.valueOf(port), directory);
        if (valgrind)
            return new BackgroundProcess(context, "sudo", "valgrind", "--trace-children=yes", "--leak-check=full", "--log-file=" + directory + "/valgrind.out", context.getSheep(), "-l7", "-d", "-p", String.valueOf(port), directory);
        // return new TimedProcess(context, 1000, "sudo", context.getSheep(), "-l7", "-d", "-p", String.valueOf(port), directory);
        return new BackgroundProcess(context, "sudo", context.getSheep(), "-f", "-l7", "-d", "-p", String.valueOf(port), directory);
    }
}

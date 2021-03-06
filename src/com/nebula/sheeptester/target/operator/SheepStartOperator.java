/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.TargetException;
import com.nebula.sheeptester.target.exec.BackgroundProcess;
import com.nebula.sheeptester.target.exec.TargetProcess;
import com.nebula.sheeptester.target.exec.TimedProcess;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.exec.Executor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author shevek
 */
public class SheepStartOperator extends AbstractProcessOperator {

    private int port;
    private String directory;
    public String cluster;
    public int vnodes = -1;
    public int zone = -1;
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
            TimedProcess process = new TimedProcess(context, 500, "sudo", "mkdir", "-pm", "755", directory + "/gcov");
            process.execute();
        }
        if (!dir.isDirectory()) {
            throw new TargetException("Failed to make directory " + dir);
        }

        return super.run(context);
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        List<String> command = new ArrayList<String>();
        command.add("sudo");
        if (strace)
            command.addAll(Arrays.asList("strace", "-f", "-o", directory + "/strace.out"));
        if (valgrind)
            command.addAll(Arrays.asList("valgrind", "--trace-children=yes", "--leak-check=full", "--log-file=" + directory + "/valgrind.out"));
        // return new TimedProcess(context, 1000, "sudo", context.getSheep(), "-l7", "-d", "-p", String.valueOf(port), directory);
        String sheep = context.getSheep();
        command.addAll(Arrays.asList(sheep, "--disable-cache", "-f", "-l7", "-d", "-p", String.valueOf(port)));
        if (cluster != null)
            command.addAll(Arrays.asList("-c", cluster));
        else if (context.getCluster() != null)
            command.addAll(Arrays.asList("-c", context.getCluster()));
        if (vnodes >= 0)
            command.addAll(Arrays.asList("-v", String.valueOf(vnodes)));
        if (zone >= 0)
            command.addAll(Arrays.asList("-z", String.valueOf(zone)));
        command.add(directory);
        TargetProcess process = new BackgroundProcess(context, command.toArray(ArrayUtils.EMPTY_STRING_ARRAY)) {

            @Override
            protected void init(Executor executor) {
                super.init(executor);
                executor.setWorkingDirectory(new File(directory));
            }
        };

        int count = 0;
        // Start from 1 to skip first '/'
        for (int i = 1; i < sheep.length(); i++)
            if (sheep.charAt(i) == '/')
                count++;
        process.getEnvironment().put("GCOV_PREFIX_STRIP", String.valueOf(count));
        process.getEnvironment().put("GCOV_PREFIX", directory + "/gcov");

        return process;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import com.nebula.sheeptester.target.exec.BackgroundProcess;
import com.nebula.sheeptester.target.exec.TargetProcess;

/**
 *
 * @author shevek
 */
public class SheepStartOperator extends AbstractProcessOperator {

    private int port;
    private String directory;

    public SheepStartOperator() {
    }

    public SheepStartOperator(int port, String directory) {
        this.port = port;
        this.directory = directory;
    }

    @Override
    protected TargetProcess newProcess(TargetContext context) {
        // -f -l7 -d -p $port /tmp/sheepdog/${port}
        return new BackgroundProcess(context, "sudo", context.getSheep(), "-f", "-l7", "-d", "-p", String.valueOf(port), directory);
    }
}

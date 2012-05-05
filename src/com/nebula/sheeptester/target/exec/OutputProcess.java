/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.exec;

import com.nebula.sheeptester.target.TargetContext;
import java.io.OutputStream;

/**
 *
 * @author shevek
 */
public class OutputProcess extends TargetProcess {

    public OutputProcess(TargetContext context, OutputStream output, String... command) {
        super(context, command);
        setOutputStream(output);
    }
}
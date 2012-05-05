/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.exec;

import com.nebula.sheeptester.target.TargetContext;
import java.io.InputStream;

/**
 *
 * @author shevek
 */
public class InputProcess extends TargetProcess {

    public InputProcess(TargetContext context, InputStream input, String... command) {
        super(context, command);
        setInputStream(input);
    }
}
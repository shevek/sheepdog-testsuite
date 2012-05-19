/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.exec;

import com.nebula.sheeptester.target.TargetContext;
import org.junit.Test;

/**
 *
 * @author shevek
 */
public class TimedProcessTest {

    @Test
    public void testTimeout() throws Exception {
        TargetContext context = new TargetContext();
        TimedProcess process = new TimedProcess(context, 5000, "/bin/sleep", "1d");
        process.execute();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

/**
 *
 * @author shevek
 */
public class TargetAssertionException extends TargetException {

    public TargetAssertionException() {
    }

    public TargetAssertionException(String message) {
        super(message);
    }

    public TargetAssertionException(Throwable cause) {
        super(cause);
    }

    public TargetAssertionException(String message, Throwable cause) {
        super(message, cause);
    }
}

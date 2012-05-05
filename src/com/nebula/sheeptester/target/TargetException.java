/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

/**
 *
 * @author shevek
 */
public class TargetException extends Exception {

    public TargetException(Throwable cause) {
        super(cause);
    }

    public TargetException(String message, Throwable cause) {
        super(message, cause);
    }

    public TargetException(String message) {
        super(message);
    }

    public TargetException() {
    }
}

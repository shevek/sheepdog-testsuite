/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller;

/**
 *
 * @author shevek
 */
public class ControllerAssertionException extends ControllerException {

    public ControllerAssertionException() {
    }

    public ControllerAssertionException(String message) {
        super(message);
    }

    public ControllerAssertionException(Throwable cause) {
        super(cause);
    }

    public ControllerAssertionException(String message, Throwable cause) {
        super(message, cause);
    }
}

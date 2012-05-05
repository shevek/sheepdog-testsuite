/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller;

import com.nebula.sheeptester.target.*;

/**
 *
 * @author shevek
 */
public class ControllerException extends Exception {

    public ControllerException(Throwable cause) {
        super(cause);
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException() {
    }
}

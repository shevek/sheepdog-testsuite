/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import java.io.OutputStream;

/**
 *
 * @author shevek
 */
public abstract class ValidatingOutputStream extends OutputStream {

    private boolean error = false;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
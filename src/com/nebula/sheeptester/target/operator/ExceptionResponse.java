/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author shevek
 */
public class ExceptionResponse extends AbstractResponse {

    private String message;

    public ExceptionResponse() {
    }

    public String getMessage() {
        return message;
    }

    public ExceptionResponse(Operator operator, Throwable throwable) {
        super(operator);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        this.message = sw.toString();
    }
}
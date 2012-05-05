/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

/**
 *
 * @author shevek
 */
public class ExceptionResponse extends AbstractResponse {

    private String message;

    public ExceptionResponse() {
    }

    public ExceptionResponse(Operator operator, Throwable throwable) {
        super(operator);
        this.message = String.valueOf(throwable);
    }
}
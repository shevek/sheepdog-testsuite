/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.exec;

/**
 *
 * @author shevek
 */
public interface ChannelProcessListener {

    public void success(ChannelProcess process);

    public void error(ChannelProcess process, String message, Throwable t);

    public void done(ChannelProcess process);
}

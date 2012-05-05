/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.util;

import com.jcraft.jsch.UserInfo;

/**
 *
 * @author shevek
 */
public class ConsoleUserInfo implements UserInfo {

    private String password;

    public ConsoleUserInfo(String password) {
        this.password = password;
    }

    @Override
    public String getPassphrase() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPassword() {
        String tmp = password;
        password = null;
        return tmp;
    }

    @Override
    public boolean promptPassword(String message) {
        if (password != null)
            return true;
        showMessage(message);
        char[] data = System.console().readPassword("Password: ");
        if (data.length == 0)
            return false;
        this.password = new String(data);
        return true;
    }

    @Override
    public boolean promptPassphrase(String message) {
        showMessage(message);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean promptYesNo(String message) {
        showMessage(message);
        if (message.contains("Are you sure you want to continue connecting?")) {
            showMessage("(Automatically typing 'yes' to acceptance prompt.)");
            return true;
        }
        if (message.contains("delete the old key")) {
            showMessage("(Automatically typing 'yes' to acceptance prompt.)");
            return true;
        }
        return System.console().readLine("Type 'yes' to continue: ").equals("yes");
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }
}

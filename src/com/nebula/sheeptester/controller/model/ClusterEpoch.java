/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author shevek
 */
public class ClusterEpoch extends ArrayList<SheepAddress> {

    public int id;
    public long time;

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        Date date = new Date(time);
        return date + " [" + id + "] " + super.toString();
    }
}

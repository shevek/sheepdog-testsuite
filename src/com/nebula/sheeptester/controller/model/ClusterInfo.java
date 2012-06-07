/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.controller.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;

/**
 *
 * @author shevek
 */
public class ClusterInfo {

    public ClusterStatus status;
    public final List<ClusterEpoch> epochs = new ArrayList<ClusterEpoch>();

    public ClusterStatus getStatus() {
        return status;
    }

    @Nonnegative
    public int getEpoch() {
        if (epochs.isEmpty())
            return 0;
        return epochs.get(0).getId();
    }

    @CheckForNull
    public ClusterEpoch getEpoch(int id) {
        for (ClusterEpoch epoch : epochs)
            if (epoch.getId() == id)
                return epoch;
        return null;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Status: ").append(status).append("\n");
        for (ClusterEpoch epoch : epochs)
            buf.append(epoch).append("\n");
        return buf.toString();
    }
}

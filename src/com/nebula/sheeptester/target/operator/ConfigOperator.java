/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.nebula.sheeptester.target.TargetContext;
import java.util.Map;

/**
 *
 * @author shevek
 */
public class ConfigOperator extends AbstractOperator {

    private String hostId;
    private String sheep;
    private String collie;
    private String cluster;
    private Map<String, String> properties;

    public ConfigOperator() {
    }

    public ConfigOperator(String hostId, String sheep, String collie, String cluster, Map<String, String> properties) {
        this.hostId = hostId;
        this.sheep = sheep;
        this.collie = collie;
        this.cluster = cluster;
    }

    public String getHostId() {
        return hostId;
    }

    public String getSheep() {
        return sheep;
    }

    public String getCollie() {
        return collie;
    }

    public String getCluster() {
        return cluster;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public AbstractResponse run(TargetContext context) throws Exception {
        context.setConfig(this);
        return new DefaultResponse(this);
    }
}

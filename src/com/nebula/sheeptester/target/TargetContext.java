/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

import com.nebula.sheeptester.target.operator.OperatorAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nebula.sheeptester.target.operator.ConfigOperator;
import com.nebula.sheeptester.target.operator.Operator;
import com.nebula.sheeptester.target.operator.ResponseAdapter;
import com.nebula.sheeptester.target.operator.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author shevek
 */
public class TargetContext {

    private Gson gson;
    private ExecutorService executor;
    private String hostId = "<unknown>";
    private String sheep = "/usr/bin/sheep";
    private String collie = "/usr/bin/collie";

    public TargetContext() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Operator.class, new OperatorAdapter());
        builder.registerTypeAdapter(Response.class, new ResponseAdapter());
        gson = builder.create();
        executor = Executors.newCachedThreadPool();
    }

    public void setConfig(@Nonnull ConfigOperator config) {
        hostId = StringUtils.defaultString(config.getHostId(), hostId);
        sheep = StringUtils.defaultString(config.getSheep(), sheep);
        collie = StringUtils.defaultString(config.getCollie(), collie);
    }

    @Nonnull
    public Gson getGson() {
        return gson;
    }

    @Nonnull
    public String getHostId() {
        return hostId;
    }

    @Nonnull
    public String getSheep() {
        return sheep;
    }

    @Nonnull
    public String getCollie() {
        return collie;
    }
}
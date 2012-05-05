/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

import com.nebula.sheeptester.target.operator.OperatorAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nebula.sheeptester.target.operator.Operator;
import com.nebula.sheeptester.target.operator.OperatorResponseAdapter;
import com.nebula.sheeptester.target.operator.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TargetContext {

    private Gson gson;
    private ExecutorService executor;

    public TargetContext() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Operator.class, new OperatorAdapter());
        builder.registerTypeAdapter(Response.class, new OperatorResponseAdapter());
        gson = builder.create();
        executor = Executors.newCachedThreadPool();
    }

    @Nonnull
    public Gson getGson() {
        return gson;
    }

    @Nonnull
    public String getCollie() {
        return "collie";
    }

    @Nonnull
    public String getSheep() {
        return "sheep";
    }
}

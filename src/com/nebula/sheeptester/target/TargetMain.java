/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target;

import com.google.gson.Gson;
import com.nebula.sheeptester.target.operator.DefaultResponse;
import com.nebula.sheeptester.target.operator.ExceptionResponse;
import com.nebula.sheeptester.target.operator.Operator;
import com.nebula.sheeptester.target.operator.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class TargetMain {

    private static final Log LOG = LogFactory.getLog(TargetMain.class);

    public static void main(String[] args) throws Exception {
        // LOG.info("Hello, world!");

        final TargetContext context = new TargetContext();
        ExecutorService executor = context.getExecutor();

        Gson gson = context.getGson();

        InputStreamReader ri = new InputStreamReader(System.in);
        BufferedReader bi = new BufferedReader(ri);
        for (;;) {
            final String input = bi.readLine();
            // LOG.info("Got input " + input);
            if (input == null)
                break;

            executor.submit(new Runnable() {

                @Override
                public void run() {
                    execute(context, input);
                }
            });
        }
    }

    private static void execute(TargetContext context, String input) {
        Gson gson = context.getGson();
        Operator operator = gson.fromJson(input, Operator.class);
        Response response;
        try {
            // LOG.info("Got operator " + operator);
            response = operator.run(context);
            // LOG.info("Got response " + response);
            if (response == null) {
                LOG.warn("Operator gave null response: " + input);
                response = new DefaultResponse(operator);
            }
        } catch (TargetAssertionException t) {
            response = new ExceptionResponse(operator, t.getMessage());
        } catch (Throwable t) {
            response = new ExceptionResponse(operator, t);
        }
        String output = gson.toJson(response, Response.class);
        // LOG.info("Send response " + output);
        System.out.println(output);
    }
}

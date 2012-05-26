/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.target.operator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author shevek
 */
public class ResponseAdapter implements JsonSerializer<Response>, JsonDeserializer<Response> {

    private static final Log LOG = LogFactory.getLog(OperatorAdapter.class);
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String PARAMETERS = "parameters";
    public static final String PACKAGE = Operator.class.getPackage().getName() + ".";

    @Override
    public JsonElement serialize(Response src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject retValue = new JsonObject();
        String typeName = src.getClass().getName();
        if (!typeName.startsWith(PACKAGE))
            throw new IllegalArgumentException("Illegal response type name " + typeName);
        if (src.getId() <= 0)
            throw new IllegalArgumentException("Illegal response id " + src.getId());
        retValue.addProperty(ID, src.getId());
        retValue.addProperty(TYPE, typeName.substring(PACKAGE.length()));
        JsonElement request = context.serialize(src);
        retValue.add(PARAMETERS, request);
        return retValue;
    }

    @Override
    public Response deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonPrimitive id = jsonObject.getAsJsonPrimitive(ID);
        if (id == null)
            throw new JsonParseException("Missing field '" + ID + "' in " + jsonObject);

        JsonPrimitive name = jsonObject.getAsJsonPrimitive(TYPE);
        if (name == null)
            throw new JsonParseException("Missing field '" + TYPE + "' in " + jsonObject);
        String typeName = PACKAGE + name.getAsString();

        JsonElement params = jsonObject.get(PARAMETERS);
        if (params == null)
            throw new JsonParseException("Missing field '" + PARAMETERS + "' in " + jsonObject);

        try {
            Class<? extends Response> type = (Class) Class.forName(typeName);
            Response response = context.deserialize(params, type);
            response.setId(id.getAsInt());
            return response;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown operator " + name, e);
        }
    }
}

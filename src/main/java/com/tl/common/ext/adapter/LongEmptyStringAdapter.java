package com.tl.common.ext.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;


public class LongEmptyStringAdapter implements JsonSerializer<Long>,JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为long类型,如果后台返回""或者null,则返回null
                return null;
            }
        } catch (Exception ignore) {
        }
        try {
            return json.getAsLong();
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}

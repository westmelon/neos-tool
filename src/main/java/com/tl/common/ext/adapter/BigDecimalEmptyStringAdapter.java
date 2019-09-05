package com.tl.common.ext.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class BigDecimalEmptyStringAdapter implements JsonSerializer<BigDecimal>,JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为integer类型,如果后台返回""或者null,则返回null
                return null;
            }
        } catch (Exception ignore) {
        }
        try {
            String str = json.getAsString();
            str = str.replaceAll(",","");
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }

    @Override
    public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }
}

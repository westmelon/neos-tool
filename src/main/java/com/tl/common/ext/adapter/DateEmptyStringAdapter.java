package com.tl.common.ext.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateEmptyStringAdapter implements JsonSerializer<Date>,JsonDeserializer<Date> {

    private final DateFormat enUsFormat;
    private final DateFormat localFormat;
    private final DateFormat iso8601Format;

    public DateEmptyStringAdapter() {
        this(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US),
                DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT));
    }

    public DateEmptyStringAdapter(String datePattern) {
        this(new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
    }

    public DateEmptyStringAdapter(int style) {
        this(DateFormat.getDateInstance(style, Locale.US), DateFormat.getDateInstance(style));
    }

    public DateEmptyStringAdapter(int dateStyle, int timeStyle) {
        this(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US),
                DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }

    public DateEmptyStringAdapter(DateFormat enUsFormat, DateFormat localFormat) {
        this.enUsFormat = enUsFormat;
        this.localFormat = localFormat;
        this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // These methods need to be synchronized since JDK DateFormat classes are not thread-safe
    // See issue 162
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        synchronized (localFormat) {
            String dateFormatAsString = enUsFormat.format(src);
            return new JsonPrimitive(dateFormatAsString);
        }
    }

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("The date should be a string value");
        }
        Date date = deserializeToDate(json);
        if (typeOfT == Date.class) {
            return date;
        } else if (typeOfT == Timestamp.class) {
            return new Timestamp(date.getTime());
        } else if (typeOfT == java.sql.Date.class) {
            return new java.sql.Date(date.getTime());
        } else {
            throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
        }
    }

    private Date deserializeToDate(JsonElement json) {
        synchronized (localFormat) {
            if(json.getAsString() == null || "".equals(json.getAsString())){
                return null;
            }
            try {
                return localFormat.parse(json.getAsString());
            } catch (ParseException ignored) {
            }
            try {
                return enUsFormat.parse(json.getAsString());
            } catch (ParseException ignored) {
            }
            try {
                return iso8601Format.parse(json.getAsString());
            } catch (ParseException e) {
                throw new JsonSyntaxException(json.getAsString(), e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DateEmptyStringAdapter.class.getSimpleName());
        sb.append('(').append(localFormat.getClass().getSimpleName()).append(')');
        return sb.toString();
    }
}

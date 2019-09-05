package com.tl.common.ext.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.tl.common.ext.adapter.*;

import java.math.BigDecimal;
import java.util.Date;

/*
 * @Description: 处理json中字段空值造成的数据转化错误
 * @Author Neo Lin
 * @Date  2018/4/10 17:51
 */
public class GsonUtils {

    private static Gson defaultEmptyStringGson;

    static{
        defaultEmptyStringGson = new GsonBuilder().registerTypeAdapter(Date.class, new DateEmptyStringAdapter("yyyy-MM-dd"))
                .registerTypeAdapter(Long.class, new LongEmptyStringAdapter())
                .registerTypeAdapter(Integer.class, new IntegerEmptyStringAdapter())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalEmptyStringAdapter())
                .registerTypeAdapter(Short.class, new ShortEmptyStringAdapter())
                .create();
    }

    public static Gson getDefaultGson(){
        return defaultEmptyStringGson;
    }

    public static Gson getDateFormatGson(String pattern){
        return new GsonBuilder().registerTypeAdapter(Date.class, new DateEmptyStringAdapter(pattern))
                .registerTypeAdapter(Long.class, new LongEmptyStringAdapter())
                .registerTypeAdapter(Integer.class, new IntegerEmptyStringAdapter())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalEmptyStringAdapter())
                .registerTypeAdapter(Short.class, new ShortEmptyStringAdapter())
                .create();
    }
}

package com.tl.common.ext.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

public class TestAdapter extends TypeAdapter<BigDecimal> {
    @Override
    public void write(JsonWriter out, BigDecimal value) throws IOException {

    }

    @Override
    public BigDecimal read(JsonReader in) throws IOException {
//        System.out.println(in.nextName());
        return new BigDecimal(in.nextDouble());
    }
}

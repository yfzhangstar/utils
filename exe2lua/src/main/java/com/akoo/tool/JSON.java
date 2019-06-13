package com.akoo.tool;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * aly @ 16-11-14.
 */
public class JSON {
    private static final Gson gson = new Gson();
    private static final Type type = new TypeToken<Object>() {
    }.getType();

    public static Object parse(String val) {
//        return com.alibaba.fastjson.JSON.parse(val);
        return gson.fromJson(val, type);

    }

    public static String JSONString(Object parse) {
        return gson.toJson(parse);
    }
}

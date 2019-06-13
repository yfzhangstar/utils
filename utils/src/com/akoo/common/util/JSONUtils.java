package com.akoo.common.util;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * File Description.
 *
 * @author Yang
 */
public class JSONUtils {
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd-HH-mm-ss")
            .create();

    public static String toJSONString(Object object) {
        Objects.requireNonNull(object);
        return gson.toJson(object);
    }

    public static <T> T fromJson(String data, Class<T> jsonClass) {
        return gson.fromJson(data, jsonClass);
    }

    public static <T> T fromJson(String data, Type jsonType) {
        return gson.fromJson(data, jsonType);
    }
}

package com.tale.utils;

import com.google.gson.Gson;

/**
 * Created by hpxl on 1/5/18.
 */
public class GsonUtils {
    private static final Gson gson = new Gson();

    public static  String toJsonString(Object object) {
        return object == null ? null : gson.toJson(object);
    }
}

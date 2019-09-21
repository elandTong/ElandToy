package com.onlyknow.toy.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class OKGsonUtil {
    public static <T> T fromJson(String json, Class<T> clazz) {

        if (TextUtils.isEmpty(json)) return null;

        try {

            return new Gson().fromJson(json, clazz);

        } catch (Exception e) {

            return null;

        }
    }

    private static class AppType implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        AppType(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}

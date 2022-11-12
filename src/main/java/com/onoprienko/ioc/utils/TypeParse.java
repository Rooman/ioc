package com.onoprienko.ioc.utils;

public class TypeParse {

    public static Object cast(Class<?> clazz, String value) {
        if (clazz == int.class ||
                clazz == Integer.class) {
            return Integer.valueOf(value);
        }
        if (clazz == short.class ||
                clazz == Short.class) {
            return Short.valueOf(value);
        }
        if (clazz == long.class ||
                clazz == Long.class) {
            return Long.valueOf(value);
        }
        if (clazz == float.class ||
                clazz == Float.class) {
            return Float.valueOf(value);
        }
        if (clazz == double.class ||
                clazz == Double.class) {
            return Double.valueOf(value);
        }
        if (clazz == boolean.class ||
                clazz == Boolean.class) {
            return Boolean.valueOf(value);
        }
        if (clazz == byte.class ||
                clazz == Byte.class) {
            return Byte.valueOf(value);
        }
        return value;
    }
}

package com.study.ioc.utils;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class TypeCast {

    public static  <T> T cast(Class<T> clazz, String value) {
        if (Objects.equals(clazz.getName(), "int") ||
                Objects.equals(clazz.getName(), "java.lang.Integer") ) {
            return (T) Integer.valueOf(value);
        }
        if (Objects.equals(clazz.getName(), "short") ||
                Objects.equals(clazz.getName(), "java.lang.Short")) {
            return (T) Short.valueOf(value);
        }
        if (Objects.equals(clazz.getName(), "long")  ||
                Objects.equals(clazz.getName(), "java.lang.Long")) {
            return (T) Long.valueOf(value);
        }
        if (Objects.equals(clazz.getName(), "float")  ||
                Objects.equals(clazz.getName(), "java.lang.Float")) {
            return (T) Float.valueOf(value);
        }
        if (Objects.equals(clazz.getName(), "double")  ||
                Objects.equals(clazz.getName(), "java.lang.Double")) {
            return (T) Double.valueOf(value);
        }
        if (Objects.equals(clazz.getName(), "boolean")  ||
                Objects.equals(clazz.getName(), "java.lang.Boolean")) {
            return (T) Boolean.valueOf(value);
        }
        return (T) value;
    }
}

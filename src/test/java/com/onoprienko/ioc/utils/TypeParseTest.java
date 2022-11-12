package com.onoprienko.ioc.utils;

import junit.framework.TestCase;

public class TypeParseTest extends TestCase {

    public void testCastIntPrimitive() throws ClassNotFoundException {
        String valueToCast = "12";

        Object castedValue = TypeParse.cast(int.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Integer");
    }

    public void testCastLongPrimitive() throws ClassNotFoundException {
        String valueToCast = "12";

        Object castedValue = TypeParse.cast(long.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Long");
    }

    public void testCastDoublePrimitive() throws ClassNotFoundException {
        String valueToCast = "12.1";

        Object castedValue = TypeParse.cast(double.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Double");
    }

    public void testCastFloatPrimitive() throws ClassNotFoundException {
        String valueToCast = "12.1";

        Object castedValue = TypeParse.cast(float.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Float");
    }

    public void testCastShortPrimitive() throws ClassNotFoundException {
        String valueToCast = "12";

        Object castedValue = TypeParse.cast(short.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Short");
    }

    public void testCastBooleanPrimitive() throws ClassNotFoundException {
        String valueToCast = "true";

        Object castedValue = TypeParse.cast(boolean.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Boolean");
        assertEquals(castedValue, Boolean.valueOf("true"));
    }

    public void testCastString() throws ClassNotFoundException {
        String valueToCast = "string";

        Object castedValue = TypeParse.cast(String.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.String");
        assertEquals(castedValue, valueToCast);
    }

    public void testCastInteger() throws ClassNotFoundException {
        String valueToCast = "12";

        Object castedValue = TypeParse.cast(Integer.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Integer");
    }

    public void testCastLong() throws ClassNotFoundException {
        String valueToCast = "12";

        Object castedValue = TypeParse.cast(Long.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Long");
    }

    public void testCastDouble() throws ClassNotFoundException {
        String valueToCast = "12.1";

        Object castedValue = TypeParse.cast(Double.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Double");
    }

    public void testCastFloat() throws ClassNotFoundException {
        String valueToCast = "12.1";

        Object castedValue = TypeParse.cast(Float.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Float");
    }

    public void testCastShort() throws ClassNotFoundException {
        String valueToCast = "12";

        Object castedValue = TypeParse.cast(Short.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Short");
    }

    public void testCastBoolean() throws ClassNotFoundException {
        String valueToCast = "true";

        Object castedValue = TypeParse.cast(Boolean.class, valueToCast);
        assertEquals(castedValue.getClass().getName(), "java.lang.Boolean");
    }
}
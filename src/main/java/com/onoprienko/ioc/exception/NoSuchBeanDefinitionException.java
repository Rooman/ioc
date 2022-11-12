package com.onoprienko.ioc.exception;

public class NoSuchBeanDefinitionException extends RuntimeException {

    public NoSuchBeanDefinitionException(String id, String clazzName, String actualClazzName) {
        super("No qualifying bean of type " + clazzName + " with id " + id + " is defined. Bean is of type " + actualClazzName);
    }

    public NoSuchBeanDefinitionException(String id) {
        super("No qualifying bean with id " + id + " is defined.");
    }
}


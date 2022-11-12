package com.onoprienko.ioc.processor.impl;

import com.onoprienko.ioc.processor.BeanPostProcessor;

import java.util.Objects;

public class CustomBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        if (Objects.equals(name, "deleteService")) {
            return 17;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (Objects.equals(name, "deleteService")) {
            return false;
        }
        return bean;
    }
}

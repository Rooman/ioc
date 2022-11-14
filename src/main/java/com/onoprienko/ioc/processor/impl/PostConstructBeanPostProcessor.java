package com.onoprienko.ioc.processor.impl;

import com.onoprienko.ioc.annotation.PostConstruct;
import com.onoprienko.ioc.entity.Bean;
import com.onoprienko.ioc.processor.BeanPostProcessor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class PostConstructBeanPostProcessor implements BeanPostProcessor {
    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(Object bean, String name) {
        Bean currentBean = (Bean) bean;
        Class<?> clazz = currentBean.getValue().getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(currentBean.getValue());
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        return bean;
    }
}

package com.onoprienko.ioc.reader;

import com.onoprienko.ioc.entity.BeanDefinition;

import java.util.Map;

public interface BeanDefinitionReader {
    Map<String, BeanDefinition> getBeanDefinition();
}

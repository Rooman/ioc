package com.onoprienko.ioc.processor;

import com.onoprienko.ioc.entity.BeanDefinition;

import java.util.List;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList);
}

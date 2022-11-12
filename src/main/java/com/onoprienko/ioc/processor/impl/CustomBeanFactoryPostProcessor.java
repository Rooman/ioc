package com.onoprienko.ioc.processor.impl;

import com.onoprienko.ioc.entity.BeanDefinition;
import com.onoprienko.ioc.processor.BeanFactoryPostProcessor;

import java.util.List;
import java.util.Map;

public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(List<BeanDefinition> beanDefinitionList) {
        for (BeanDefinition beanDefinition : beanDefinitionList) {
            if (beanDefinition.getId().equals("employeeService")) {
                beanDefinition.setClassName("com.onoprienko.ioc.entity.NewEmployeeService");
                beanDefinition.setValueDependencies(Map.of("employee", "Karina",
                        "salary", "1000"));
            }
        }

    }
}

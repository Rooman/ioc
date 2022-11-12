package com.onoprienko.ioc.processor.impl;

import com.onoprienko.ioc.entity.BeanDefinition;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class CustomBeanFactoryPostProcessorTest extends TestCase {

    public void testPostProcessBeanFactory() {
        CustomBeanFactoryPostProcessor customBeanFactoryPostProcessor = new CustomBeanFactoryPostProcessor();
        List<BeanDefinition> definitions = new ArrayList<>();
        BeanDefinition beanDefinitionEmployee = new BeanDefinition("employeeService", "com.onoprienko.ioc.entity.EmployeeService");
        BeanDefinition beanDefinitionDelete = new BeanDefinition("deleteService", "com.onoprienko.ioc.entity.DeleteService");
        definitions.add(beanDefinitionEmployee);
        definitions.add(beanDefinitionDelete);

        customBeanFactoryPostProcessor.postProcessBeanFactory(definitions);

        assertEquals(definitions.size(), 2);
        assertEquals(definitions.get(0).getId(), "employeeService");
        assertEquals(definitions.get(1).getId(), "deleteService");
        assertEquals(definitions.get(0).getClassName(), "com.onoprienko.ioc.entity.NewEmployeeService");
        assertEquals(definitions.get(1).getClassName(), "com.onoprienko.ioc.entity.DeleteService");
    }
}
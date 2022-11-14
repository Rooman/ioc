package com.onoprienko.ioc.processor.impl;

import com.onoprienko.ioc.entity.Bean;
import com.onoprienko.ioc.entity.NewEmployeeService;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class PostConstructBeanPostProcessorTest {
    @Test
    public void runPostConstruct() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PostConstructBeanPostProcessor postProcessor = new PostConstructBeanPostProcessor();
        Bean bean = new Bean("newEmployee", new NewEmployeeService("Anna", 600));
        Method getMethod = bean.getValue().getClass().getDeclaredMethod("getSalary");
        int oldSalary = (int) getMethod.invoke(bean.getValue());
        assertEquals(oldSalary, 600);
        postProcessor.postProcessBeforeInitialization(bean, bean.getId());
        int newSalary = (int) getMethod.invoke(bean.getValue());
        assertEquals(newSalary, 800);
    }

    @Test
    public void postProcessAfterInitialization() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PostConstructBeanPostProcessor postProcessor = new PostConstructBeanPostProcessor();
        Bean bean = new Bean("newEmployee", new NewEmployeeService("Anna", 600));
        Method getMethod = bean.getValue().getClass().getDeclaredMethod("getSalary");
        int oldSalary = (int) getMethod.invoke(bean.getValue());
        assertEquals(oldSalary, 600);
        postProcessor.postProcessAfterInitialization(bean, bean.getId());
        int newSalary = (int) getMethod.invoke(bean.getValue());
        assertEquals(newSalary, 600);
    }
}
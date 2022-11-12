package com.onoprienko.ioc.processor.impl;

import com.onoprienko.ioc.entity.Bean;
import com.onoprienko.ioc.entity.DeleteService;
import com.onoprienko.ioc.entity.EmployeeService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomBeanPostProcessorTest {

    @Test
    public void postProcessBeforeInitialization() {
        CustomBeanPostProcessor customBeanPostProcessor = new CustomBeanPostProcessor();
        Bean deleteServiceBean = new Bean("deleteService", new DeleteService());
        Bean employeeServiceBean = new Bean("employee", new EmployeeService());

        Object deleteService = customBeanPostProcessor.postProcessBeforeInitialization(deleteServiceBean, "deleteService");
        Object employee = customBeanPostProcessor.postProcessBeforeInitialization(employeeServiceBean, "employee");

        assertEquals(deleteService, 17);
        assertEquals(employee, employeeServiceBean);
    }

    @Test
    public void postProcessAfterInitialization() {
        CustomBeanPostProcessor customBeanPostProcessor = new CustomBeanPostProcessor();
        Bean deleteServiceBean = new Bean("deleteService", new DeleteService());
        Bean employeeServiceBean = new Bean("employee", new EmployeeService());

        Object deleteService = customBeanPostProcessor.postProcessAfterInitialization(deleteServiceBean, "deleteService");
        Object employee = customBeanPostProcessor.postProcessAfterInitialization(employeeServiceBean, "employee");

        assertEquals(deleteService, false);
        assertEquals(employee, employeeServiceBean);
    }
}
package com.onoprienko.ioc.context.impl;

import com.onoprienko.ioc.entity.*;
import com.onoprienko.ioc.exception.BeanInstantiationException;
import com.onoprienko.ioc.exception.NoSuchBeanDefinitionException;
import com.onoprienko.ioc.exception.NoUniqueBeanOfTypeException;
import com.onoprienko.ioc.exception.NotWritablePropertyException;
import com.onoprienko.ioc.processor.impl.CustomBeanFactoryPostProcessor;
import com.onoprienko.ioc.processor.impl.CustomBeanPostProcessor;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GenericApplicationContextTest {

    private GenericApplicationContext genericApplicationContext;

    @Before
    public void before() {
        genericApplicationContext = new GenericApplicationContext();
    }

    @Test
    public void testCreateBeans() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService = new BeanDefinition("mailServicePOP", "com.onoprienko.ioc.entity.MailService");
        beanDefinitionMap.put("mailServicePOP", beanDefinitionMailService);
        BeanDefinition beanDefinitionUserService = new BeanDefinition("userService", "com.onoprienko.ioc.entity.DefaultUserService");
        beanDefinitionMap.put("userService", beanDefinitionUserService);

        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);

        Bean actualMailBean = beanMap.get("mailServicePOP");
        assertNotNull(actualMailBean);
        assertEquals("mailServicePOP", actualMailBean.getId());
        assertEquals(MailService.class, actualMailBean.getValue().getClass());

        Bean actualUserBean = beanMap.get("userService");
        assertNotNull(actualUserBean);
        assertEquals("userService", actualUserBean.getId());
        assertEquals(DefaultUserService.class, actualUserBean.getValue().getClass());
    }


    @Test(expected = BeanInstantiationException.class)
    public void testCreateBeansWithWrongClass() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition errorBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.entity.TestClass");
        beanDefinitionMap.put("mailServicePOP", errorBeanDefinition);
        Map<String, Bean> beanMap = genericApplicationContext.createBeans(beanDefinitionMap);
    }

    @Test
    public void testGetBeanById() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        DefaultUserService beanValue2 = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        genericApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = (DefaultUserService) genericApplicationContext.getBean("bean1");
        DefaultUserService actualBeanValue2 = (DefaultUserService) genericApplicationContext.getBean("bean2");
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test
    public void testGetBeanByClazz() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        MailService beanValue2 = new MailService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        genericApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = genericApplicationContext.getBean(DefaultUserService.class);
        MailService actualBeanValue2 = genericApplicationContext.getBean(MailService.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test(expected = NoUniqueBeanOfTypeException.class)
    public void testGetBeanByClazzNoUniqueBean() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean1", new Bean("bean1", new DefaultUserService()));
        beanMap.put("bean2", new Bean("bean2", new DefaultUserService()));
        genericApplicationContext.setBeans(beanMap);
        genericApplicationContext.getBean(DefaultUserService.class);
    }

    @Test
    public void testGetBeanByIdAndClazz() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        DefaultUserService beanValue2 = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        genericApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = genericApplicationContext.getBean("bean1", DefaultUserService.class);
        DefaultUserService actualBeanValue2 = genericApplicationContext.getBean("bean2", DefaultUserService.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }


    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testGetBeanByIdAndClazzNoSuchBean() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue));
        genericApplicationContext.setBeans(beanMap);
        genericApplicationContext.getBean("bean1", MailService.class);

    }

    @Test
    public void getBeanNames() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean3", new Bean("bean3", new DefaultUserService()));
        beanMap.put("bean4", new Bean("bean4", new DefaultUserService()));
        beanMap.put("bean5", new Bean("bean5", new DefaultUserService()));
        genericApplicationContext.setBeans(beanMap);
        List<String> actualBeansNames = genericApplicationContext.getBeanNames();
        List<String> expectedBeansNames = Arrays.asList("bean3", "bean4", "bean5");
        assertTrue(actualBeansNames.containsAll(expectedBeansNames));
        assertTrue(expectedBeansNames.containsAll(actualBeansNames));
    }

    @Test
    public void testInjectValueDependencies() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailService mailServicePOP = new MailService();
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
        MailService mailServiceIMAP = new MailService();
        beanMap.put("mailServiceIMAP", new Bean("mailServiceIMAP", mailServiceIMAP));

        //  setPort(110) and setProtocol("POP3") via valueDependencies
        BeanDefinition popServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.onoprienko.ioc.entity.MailService");
        Map<String, String> popServiceValueDependencies = new HashMap<>();
        popServiceValueDependencies.put("port", "110");
        popServiceValueDependencies.put("protocol", "POP3");
        popServiceBeanDefinition.setValueDependencies(popServiceValueDependencies);
        beanDefinitionMap.put("mailServicePOP", popServiceBeanDefinition);

        //  setPort(143) and setProtocol("IMAP") via valueDependencies
        BeanDefinition imapServiceBeanDefinition = new BeanDefinition("mailServiceIMAP", "com.onoprienko.ioc.entity.MailService");
        Map<String, String> imapServiceValueDependencies = new HashMap<>();
        imapServiceValueDependencies.put("port", "143");
        imapServiceValueDependencies.put("protocol", "IMAP");
        imapServiceBeanDefinition.setValueDependencies(imapServiceValueDependencies);
        beanDefinitionMap.put("mailServiceIMAP", imapServiceBeanDefinition);

        genericApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);
        assertEquals(110, mailServicePOP.getPort());
        assertEquals("POP3", mailServicePOP.getProtocol());
        assertEquals(143, mailServiceIMAP.getPort());
        assertEquals("IMAP", mailServiceIMAP.getProtocol());
    }

    @Test(expected = NotWritablePropertyException.class)
    public void testInjectValueDependenciesThrowsException() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailServiceWithoutSetters mailServicePOP = new MailServiceWithoutSetters();
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
        MailServiceWithoutSetters mailServiceIMAP = new MailServiceWithoutSetters();
        beanMap.put("mailServiceIMAP", new Bean("mailServiceIMAP", mailServiceIMAP));

        //  setPort(110) and setProtocol("POP3") via valueDependencies
        BeanDefinition popServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.onoprienko.ioc.entity.MailService");
        Map<String, String> popServiceValueDependencies = new HashMap<>();
        popServiceValueDependencies.put("port", "110");
        popServiceValueDependencies.put("protocol", "POP3");
        popServiceBeanDefinition.setValueDependencies(popServiceValueDependencies);
        beanDefinitionMap.put("mailServicePOP", popServiceBeanDefinition);

        //  setPort(143) and setProtocol("IMAP") via valueDependencies
        BeanDefinition imapServiceBeanDefinition = new BeanDefinition("mailServiceIMAP", "com.onoprienko.ioc.entity.MailService");
        Map<String, String> imapServiceValueDependencies = new HashMap<>();
        imapServiceValueDependencies.put("port", "143");
        imapServiceValueDependencies.put("protocol", "IMAP");
        imapServiceBeanDefinition.setValueDependencies(imapServiceValueDependencies);
        beanDefinitionMap.put("mailServiceIMAP", imapServiceBeanDefinition);

        genericApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);
    }

    @Test
    public void testInjectRefDependencies() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailService mailServicePOP = new MailService();
        mailServicePOP.setPort(110);
        mailServicePOP.setProtocol("POP3");
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));

        DefaultUserService userService = new DefaultUserService();
        beanMap.put("userService", new Bean("userService", userService));

        //  setMailService(mailServicePOP) via refDependencies
        BeanDefinition userServiceBeanDefinition = new BeanDefinition("userService", "com.onoprienko.ioc.entity.DefaultUserService");
        BeanDefinition mailServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.onoprienko.ioc.entity.MailService");
        Map<String, String> userServiceRefDependencies = new HashMap<>();
        userServiceRefDependencies.put("mailService", "mailServicePOP");
        userServiceBeanDefinition.setRefDependencies(userServiceRefDependencies);
        beanDefinitionMap.put("userService", userServiceBeanDefinition);
        beanDefinitionMap.put("mailServicePOP", mailServiceBeanDefinition);

        genericApplicationContext.injectRefDependencies(beanDefinitionMap, beanMap);
        assertNotNull(userService.getMailService());
        assertEquals(110, ((MailService) userService.getMailService()).getPort());
        assertEquals("POP3", ((MailService) userService.getMailService()).getProtocol());
    }

    @Test(expected = NotWritablePropertyException.class)
    public void testInjectRefDependenciesThrowsException() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        MailService mailServicePOP = new MailService();
        mailServicePOP.setPort(110);
        mailServicePOP.setProtocol("POP3");
        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));

        UserServiceWithoutSetters userService = new UserServiceWithoutSetters();
        beanMap.put("userService", new Bean("userService", userService));

        //  setMailService(mailServicePOP) via refDependencies
        BeanDefinition userServiceBeanDefinition = new BeanDefinition("userService", "com.onoprienko.ioc.entity.UserServiceWithoutSetters");
        BeanDefinition mailServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.onoprienko.ioc.entity.MailService");
        Map<String, String> userServiceRefDependencies = new HashMap<>();
        userServiceRefDependencies.put("mailService", "mailServicePOP");
        userServiceBeanDefinition.setRefDependencies(userServiceRefDependencies);
        beanDefinitionMap.put("userService", userServiceBeanDefinition);
        beanDefinitionMap.put("mailServicePOP", mailServiceBeanDefinition);

        genericApplicationContext.injectRefDependencies(beanDefinitionMap, beanMap);
    }

    @Test
    public void testInjectRefDependenciesWithClassWithoutInterface() {
        Map<String, Bean> beanMap = new HashMap<>();
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        EmployeeService employeeService = new EmployeeService();
        employeeService.setEmployee("Tom");
        beanMap.put("employeeService", new Bean("employeeService", employeeService));

        WorkService workService = new WorkService();
        beanMap.put("workService", new Bean("workService", workService));

        //  setMailService(mailServicePOP) via refDependencies
        BeanDefinition workServiceBeanDefinition = new BeanDefinition("workService", "com.onoprienko.ioc.entity.WorkService");
        BeanDefinition employeeServiceBeanDefinition = new BeanDefinition("employeeService", "com.onoprienko.ioc.entity.EmployeeService");
        Map<String, String> workServiceRefDependencies = new HashMap<>();
        workServiceRefDependencies.put("employeeService", "employeeService");
        workServiceBeanDefinition.setRefDependencies(workServiceRefDependencies);
        beanDefinitionMap.put("workService", workServiceBeanDefinition);
        beanDefinitionMap.put("employeeService", employeeServiceBeanDefinition);

        genericApplicationContext.injectRefDependencies(beanDefinitionMap, beanMap);
        assertNotNull(workService.getEmployeeService());
        assertEquals("Tom", (workService.getEmployeeService().getEmployee()));
    }

    @Test
    public void testInjectValue() throws ReflectiveOperationException {
        MailService mailService = new MailService();
        Method setPortMethod = MailService.class.getDeclaredMethod("setPort", Integer.TYPE);
        genericApplicationContext.injectValue(mailService, setPortMethod, "465");
        int actualPort = mailService.getPort();
        assertEquals(465, actualPort);
    }

    @Test
    public void getBeanClassTypeReturnInterface() {
        DefaultUserService defaultUserService = new DefaultUserService();
        Bean bean = new Bean("userService", defaultUserService);
        Class<?> beanClassType = genericApplicationContext.getBeanClassType(bean);
        assertEquals(beanClassType.getName(), "com.onoprienko.ioc.entity.UserService");
    }

    @Test
    public void getBeanClassTypeReturnClass() {
        WorkService workService = new WorkService();
        Bean bean = new Bean("workService", workService);
        Class<?> beanClassType = genericApplicationContext.getBeanClassType(bean);
        assertEquals(beanClassType.getName(), "com.onoprienko.ioc.entity.WorkService");
    }

    @Test
    public void genericApplicationContextTestConstructorWithPath() {
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext("context.xml");

        DefaultUserService userService = (DefaultUserService) genericApplicationContext.getBean("userService");
        MailService mailService = genericApplicationContext.getBean("mailServiceIMAP", MailService.class);
        NewEmployeeService newEmployeeService = genericApplicationContext.getBean("employeeService", NewEmployeeService.class);
        assertNotNull(userService.getMailService());

        assertEquals(mailService.getPort(), 143);
        assertEquals(mailService.getProtocol(), "IMAP");
        assertNotNull(newEmployeeService);
        assertEquals(newEmployeeService.getSalary(), 1200);
    }


    @Test
    public void getSystemBeans() {
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext("context.xml");
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionEmployee = new BeanDefinition("employeeService", "com.onoprienko.ioc.entity.EmployeeService");
        BeanDefinition beanDefinitionFactory = new BeanDefinition("beanFactoryPostProcessor", "com.onoprienko.ioc.processor.impl.CustomBeanFactoryPostProcessor");
        BeanDefinition beanDefinitionPost = new BeanDefinition("beanPostProcessor", "com.onoprienko.ioc.processor.impl.CustomBeanPostProcessor");
        BeanDefinition beanDefinitionDelete = new BeanDefinition("deleteService", "com.onoprienko.ioc.entity.DeleteService");
        beanDefinitionMap.put("employeeService", beanDefinitionEmployee);
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactory);
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPost);
        beanDefinitionMap.put("deleteService", beanDefinitionDelete);

        assertEquals(beanDefinitionMap.size(), 4);


        SystemBeans systemBeans = genericApplicationContext.getSystemBeans(beanDefinitionMap);

        assertNotNull(systemBeans);
        assertEquals(systemBeans.getBeanPostProcessors().size(), 1);
        assertEquals(systemBeans.getBeanFactoryPostProcessors().size(), 1);

        assertEquals(beanDefinitionMap.size(), 2);

        assertEquals(systemBeans.getBeanFactoryPostProcessors().get(0).getClass(), CustomBeanFactoryPostProcessor.class);
        assertEquals(systemBeans.getBeanPostProcessors().get(0).getClass(), CustomBeanPostProcessor.class);
    }
}

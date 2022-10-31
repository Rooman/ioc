package com.study.ioc.context.impl;

import com.study.ioc.context.ApplicationContext;
import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.exception.BeanInstantiationException;
import com.study.ioc.exception.NoSuchBeanDefinitionException;
import com.study.ioc.exception.NoUniqueBeanOfTypeException;
import com.study.ioc.exception.NotWritablePropertyException;
import com.study.ioc.reader.BeanDefinitionReader;
import com.study.ioc.reader.sax.XmlBeanDefinitionReader;
import com.study.ioc.utils.TypeCast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenericApplicationContext implements ApplicationContext {

    private Map<String, Bean> beans;

    GenericApplicationContext() {
    }

    public GenericApplicationContext(String... paths) {
        this(new XmlBeanDefinitionReader(paths));
    }

    public GenericApplicationContext(BeanDefinitionReader definitionReader) {
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();

        beans = createBeans(beanDefinitions);
        injectValueDependencies(beanDefinitions, beans);
        injectRefDependencies(beanDefinitions, beans);
    }

    @Override
    public Object getBean(String beanId) {
        return beans.get(beanId).getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        List<Bean> beansValues = beans.values().stream().toList();
        List<Bean> result = beansValues.stream()
                .filter(bean -> Objects.equals(bean.getValue().getClass(), clazz))
                .toList();
        if (result.size() > 1) {
            throw new NoUniqueBeanOfTypeException("No unique bean of type " + clazz.getName());
        }
        return (T) result.get(0).getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String id, Class<T> clazz) {
        Bean bean = beans.get(id);
        if (Objects.equals(bean.getValue().getClass(), clazz)) {
            return (T) bean.getValue();
        }
        throw new NoSuchBeanDefinitionException(id, clazz.getName(), bean.getValue().getClass().getName());
    }

    @Override
    public List<String> getBeanNames() {
        return beans.keySet().stream().toList();
    }

    Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        Map<String, Bean> beansMap = new HashMap<>();
        beanDefinitionMap.forEach((beanId, beanDefinition) -> {
            try {
                Object beanValue = Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance();
                beansMap.put(beanId, new Bean(beanId, beanValue));
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new BeanInstantiationException(e.getMessage(), e);
            }
        });
        return beansMap;
    }

    void injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        List<Bean> listBeans = beans.values().stream().toList();

        for (Bean currentBean : listBeans) {
            BeanDefinition beanDefinition = beanDefinitions.get(currentBean.getId());
            Map<String, String> valueDependencies = beanDefinition.getValueDependencies();
            valueDependencies.forEach((name, value) -> {
                try {
                    Object bean = currentBean.getValue();
                    Class<?> valueType = bean.getClass().getDeclaredField(name).getType();
                    Method injectMethod = bean.getClass().getDeclaredMethod(getSetterName(name), valueType);
                    injectValue(bean, injectMethod, value);
                } catch (ReflectiveOperationException e) {
                    throw new NotWritablePropertyException(e.getMessage(), e);
                }
            });

        }
    }

    void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        List<Bean> listBeans = beans.values().stream().toList();
        for (Bean currentBean : listBeans) {
            BeanDefinition beanDefinition = beanDefinitions.get(currentBean.getId());
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();
            if(refDependencies!=null) {
                refDependencies.forEach((name, beanRefId) -> {
                    Bean refBean = beans.get(beanRefId);
                    Object bean = currentBean.getValue();
                    try {
                        Method injectMethod = bean.getClass().getDeclaredMethod(getSetterName(name), getBeanClassType(refBean));
                        injectMethod.invoke(bean, refBean.getValue());
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new NotWritablePropertyException(e.getMessage(), e);
                    }
                });
            }
        }
    }


    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    void injectValue(Object object, Method classMethod, String propertyValue) throws ReflectiveOperationException {
        Class<?> parameterType = classMethod.getParameterTypes()[0];
        classMethod.invoke(object, TypeCast.cast(parameterType, propertyValue));
    }

    void setBeans(Map<String, Bean> beans) {
        this.beans = beans;
    }

    Class<?> getBeanClassType(Bean bean) {
        Class<?>[] interfaces = bean.getValue().getClass().getInterfaces();
        if (interfaces.length == 0) {
            return bean.getValue().getClass();
        }
        return interfaces[0];
    }
}

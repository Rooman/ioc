package com.study.ioc.context.impl;

import com.study.ioc.context.ApplicationContext;
import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.exception.*;
import com.study.ioc.reader.BeanDefinitionReader;
import com.study.ioc.reader.sax.XmlBeanDefinitionReader;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return !beans.containsKey(beanId) ? null : beans.get(beanId).getValue();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        T beanValue = null;
        for (Map.Entry<String, Bean> entry : beans.entrySet()) {
            Bean bean = entry.getValue();
            if (clazz.isAssignableFrom(bean.getValue().getClass())) {
                if (beanValue != null) {
                    throw new NoUniqueBeanOfTypeException("No unique bean of type " + clazz.getName());
                }
                beanValue = clazz.cast(bean.getValue());
            }
        }
        return beanValue;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        Object bean = getBean(id);
        if (bean == null) {
            return null;
        }
        if (clazz.isAssignableFrom(bean.getClass())) {
            return clazz.cast(bean);
        }
        throw new NoSuchBeanDefinitionException(id, clazz.getName(), bean.getClass().toString());
    }

    @Override
    public List<String> getBeanNames() {
        return new ArrayList<>(beans.keySet());
    }

    Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        Map<String, Bean> createdBeanMap = new HashMap<>();
        beanDefinitionMap.forEach((id, beanDefinition) -> {
            try {
                Class<?> instanceClass = Class.forName(beanDefinition.getClassName());
                Object value = instanceClass.getConstructor().newInstance();
                createdBeanMap.put(id, new Bean(id, value));
            } catch (ReflectiveOperationException e) {
                throw new BeanInstantiationException("Could not instantiate bean class " + beanDefinition.getClassName(), e);
            }
        });
        return createdBeanMap;
    }

    void injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitions.entrySet()) {
            BeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            for (Map.Entry<String, String> valueDependencyEntry : beanDefinition.getValueDependencies().entrySet()) {
                String setterName = getSetterName(valueDependencyEntry.getKey());
                try {
                    Class<?> instanceClass = Class.forName(beanDefinition.getClassName());
                    for (Method classMethod : instanceClass.getDeclaredMethods()) {
                        if (classMethod.getName().equals(setterName)) {
                            String beanDefinitionKey = beanDefinitionEntry.getKey();
                            Object beanValue = beans.get(beanDefinitionKey).getValue();
                            String dependencyValue = valueDependencyEntry.getValue();
                            injectValue(beanValue, classMethod, dependencyValue);
                            break;
                        }
                    }
                } catch (ReflectiveOperationException e) {
                    throw new BeanInstantiationException("Could not set value dependencies for bean class " + beanDefinition.getClassName(), e);
                }
            }
        }
    }

    void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinitions.entrySet()) {
            BeanDefinition beanDefinition = beanDefinitionEntry.getValue();
            for (Map.Entry<String, String> refDependencyEntry : beanDefinition.getRefDependencies().entrySet()) {
                String setterName = getSetterName(refDependencyEntry.getKey());
                try {
                    for (Method classMethod : Class.forName(beanDefinition.getClassName()).getDeclaredMethods()) {
                        if (classMethod.getName().equals(setterName)) {
                            String beanDefinitionKey = beanDefinitionEntry.getKey();
                            Object beanValue = beans.get(beanDefinitionKey).getValue();
                            String refValue = refDependencyEntry.getValue();
                            Object refBeanValue = beans.get(refValue).getValue();
                            classMethod.invoke(beanValue, refBeanValue);
                            break;
                        }
                    }
                } catch (ReflectiveOperationException e) {
                    throw new BeanInstantiationException("Could not set ref dependencies for bean class " + beanDefinition.getClassName(), e);
                }
            }
        }
    }



    void processPostConstruct(Map<String, Bean> beans) {
        beans.forEach((id, bean) -> {
            Method[] methods = bean.getValue().getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class) && method.getParameterCount() == 0) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean.getValue());
                    } catch (ReflectiveOperationException e) {
                        throw new ProcessPostConstructException("Could not process PostConstruct", e);
                    }
                }
            }
        });

    }

    String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    void injectValue(Object object, Method classMethod, String propertyValue) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = classMethod.getParameterTypes();
        Class parameterType = parameterTypes[0];
        Object value;
        if (parameterType.isPrimitive()) {
            if (parameterType == Integer.TYPE) {
                value = Integer.parseInt(propertyValue);
            } else if (parameterType == Double.TYPE) {
                value = Double.parseDouble(propertyValue);
            } else if (parameterType == Float.TYPE) {
                value = Float.parseFloat(propertyValue);
            } else if (parameterType == Byte.TYPE) {
                value = Byte.parseByte(propertyValue);
            } else if (parameterType == Short.TYPE) {
                value = Short.parseShort(propertyValue);
            } else if (parameterType == Long.TYPE) {
                value = Long.parseLong(propertyValue);
            } else if (parameterType == Boolean.TYPE) {
                value = Boolean.parseBoolean(propertyValue);
            } else if (parameterType == Character.TYPE) {
                value = (propertyValue.charAt(0));
            } else {
                value = propertyValue;
            }
        } else {
            value = parameterType.cast(propertyValue);
        }
        classMethod.invoke(object, value);
    }

    void setBeans(Map<String, Bean> beans) {
        this.beans = beans;
    }
}

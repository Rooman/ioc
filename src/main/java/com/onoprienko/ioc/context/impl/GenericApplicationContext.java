package com.onoprienko.ioc.context.impl;

import com.onoprienko.ioc.annotation.PostConstruct;
import com.onoprienko.ioc.context.ApplicationContext;
import com.onoprienko.ioc.entity.Bean;
import com.onoprienko.ioc.entity.BeanDefinition;
import com.onoprienko.ioc.entity.SystemBeans;
import com.onoprienko.ioc.exception.BeanInstantiationException;
import com.onoprienko.ioc.exception.NoSuchBeanDefinitionException;
import com.onoprienko.ioc.exception.NoUniqueBeanOfTypeException;
import com.onoprienko.ioc.exception.NotWritablePropertyException;
import com.onoprienko.ioc.processor.BeanFactoryPostProcessor;
import com.onoprienko.ioc.processor.BeanPostProcessor;
import com.onoprienko.ioc.reader.BeanDefinitionReader;
import com.onoprienko.ioc.reader.sax.XmlBeanDefinitionReader;
import com.onoprienko.ioc.utils.TypeParse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@NoArgsConstructor
@Setter
public class GenericApplicationContext implements ApplicationContext {

    private Map<String, Bean> beans;

    public GenericApplicationContext(String... paths) {
        this(new XmlBeanDefinitionReader(paths));
    }

    public GenericApplicationContext(BeanDefinitionReader definitionReader) {
        log.info("Getting bean definitions");
        Map<String, BeanDefinition> beanDefinitions = definitionReader.getBeanDefinition();

        log.info("Creating system beans");
        SystemBeans systemBeans = getSystemBeans(beanDefinitions);

        systemBeans.getBeanFactoryPostProcessors().forEach(beanFactoryPostProcessor ->
                beanFactoryPostProcessor.postProcessBeanFactory(beanDefinitions.values().stream().toList()));

        log.info("Creating beans");
        beans = createBeans(beanDefinitions);

        log.info("Injecting value dependencies in beans");
        injectValueDependencies(beanDefinitions, beans);

        log.info("Injecting ref dependencies in beans");
        injectRefDependencies(beanDefinitions, beans);


        log.info("Run post process before initialization");
        beans.forEach((id, bean) ->
                systemBeans.getBeanPostProcessors().forEach(beanPostProcessor
                        -> beanPostProcessor.postProcessBeforeInitialization(bean, id))
        );

        log.info("Run initialization with @PostConstruct");
        beans.forEach((s, bean) -> runPostConstruct(bean));

        log.info("Run post process after initialization");
        beans.forEach((id, bean) ->
                systemBeans.getBeanPostProcessors().forEach(beanPostProcessor
                        -> beanPostProcessor.postProcessAfterInitialization(bean, id))
        );

        log.info("Generic Application Context successfully created");
    }

    @Override
    public Object getBean(String beanId) {
        Object bean = beans.get(beanId).getValue();
        if (bean == null) {
            log.error("No bean with id: {}", beanId);
            throw new NoSuchBeanDefinitionException(beanId);
        }
        log.info("Find bean with id: {}", beanId);
        return bean;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        List<Bean> result = beans.values().stream()
                .filter(bean -> Objects.equals(bean.getValue().getClass(), clazz))
                .toList();
        if (result.size() > 1) {
            throw new NoUniqueBeanOfTypeException("No unique bean of type " + clazz.getName());
        }
        log.info("Find bean with class {}", clazz.getName());
        return clazz.cast(result.get(0).getValue());
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        Bean bean = beans.get(id);
        if (Objects.equals(bean.getValue().getClass(), clazz)) {
            log.info("Find bean with class {} and id {}", clazz.getName(), id);
            return clazz.cast(bean.getValue());
        }
        log.error("No bean with class {} and id {}", clazz.getName(), id);
        throw new NoSuchBeanDefinitionException(id, clazz.getName(), bean.getValue().getClass().getName());
    }

    @Override
    public List<String> getBeanNames() {
        List<String> beansNames = beans.keySet().stream().toList();
        log.info("All beans names: {}", beansNames);
        return beansNames;
    }

    Map<String, Bean> createBeans(Map<String, BeanDefinition> beanDefinitionMap) {
        Map<String, Bean> beansMap = new HashMap<>();
        beanDefinitionMap.forEach((beanId, beanDefinition) -> {
            try {
                Object beanValue = Class.forName(beanDefinition.getClassName()).getDeclaredConstructor().newInstance();
                beansMap.put(beanId, new Bean(beanId, beanValue));
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                log.error("Error while creating beans", e);
                throw new BeanInstantiationException(e.getMessage(), e);
            }
        });
        log.info("Beans created {}", beansMap);
        return beansMap;
    }

    void injectValueDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        beans.values().forEach(currentBean -> {
            BeanDefinition beanDefinition = beanDefinitions.get(currentBean.getId());
            Map<String, String> valueDependencies = beanDefinition.getValueDependencies();
            valueDependencies.forEach((name, value) -> {
                try {
                    Object bean = currentBean.getValue();
                    Class<?> valueType = bean.getClass().getDeclaredField(name).getType();
                    Method injectMethod = bean.getClass().getDeclaredMethod(getSetterName(name), valueType);
                    injectValue(bean, injectMethod, value);
                } catch (ReflectiveOperationException e) {
                    log.error("Error while injecting value dependencies", e);
                    throw new NotWritablePropertyException(e.getMessage(), e);
                }
            });
        });
        log.info("Value dependencies injected");
    }

    void injectRefDependencies(Map<String, BeanDefinition> beanDefinitions, Map<String, Bean> beans) {
        beans.values().forEach(currentBean -> {
            BeanDefinition beanDefinition = beanDefinitions.get(currentBean.getId());
            Map<String, String> refDependencies = beanDefinition.getRefDependencies();
            if (refDependencies != null) {
                refDependencies.forEach((name, beanRefId) -> {
                    Bean refBean = beans.get(beanRefId);
                    Object bean = currentBean.getValue();
                    try {
                        Method injectMethod = bean.getClass().getDeclaredMethod(getSetterName(name), getBeanClassType(refBean));
                        injectMethod.invoke(bean, refBean.getValue());
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        log.error("Error while injecting ref dependencies", e);
                        throw new NotWritablePropertyException(e.getMessage(), e);
                    }
                });
            }
        });
        log.info("Ref dependencies injected");
    }


    private String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    void injectValue(Object object, Method classMethod, String propertyValue) throws ReflectiveOperationException {
        Class<?> parameterType = classMethod.getParameterTypes()[0];
        classMethod.invoke(object, TypeParse.cast(parameterType, propertyValue));
    }

    Class<?> getBeanClassType(Bean bean) {
        Class<?>[] interfaces = bean.getValue().getClass().getInterfaces();
        if (interfaces.length == 0) {
            return bean.getValue().getClass();
        }
        return interfaces[0];
    }


    @SneakyThrows
    void runPostConstruct(Bean bean) {
        Class<?> clazz = bean.getValue().getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(bean.getValue());
            }
        }
    }


    @SneakyThrows
    SystemBeans getSystemBeans(Map<String, BeanDefinition> beanDefinitions) {
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
        List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
        Collection<BeanDefinition> values = beanDefinitions.values();
        Iterator<BeanDefinition> iterator = values.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            Class<?> clazz = Class.forName(beanDefinition.getClassName());
            List<Class<?>> clazzInterfaces = List.of(clazz.getInterfaces());
            for (Class<?> clazzInterface : clazzInterfaces) {
                if (Objects.equals(clazzInterface, BeanPostProcessor.class)) {
                    iterator.remove();
                    beanPostProcessors.add((BeanPostProcessor) clazz.getDeclaredConstructor().newInstance());
                } else if (Objects.equals(clazzInterface, BeanFactoryPostProcessor.class)) {
                    iterator.remove();
                    beanFactoryPostProcessors.add((BeanFactoryPostProcessor) clazz.getDeclaredConstructor().newInstance());
                }
            }
        }


        return new SystemBeans(beanPostProcessors, beanFactoryPostProcessors);
    }
}

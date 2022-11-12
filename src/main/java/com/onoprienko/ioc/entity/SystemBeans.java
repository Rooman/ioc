package com.onoprienko.ioc.entity;

import com.onoprienko.ioc.processor.BeanFactoryPostProcessor;
import com.onoprienko.ioc.processor.BeanPostProcessor;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SystemBeans {
    private List<BeanPostProcessor> beanPostProcessors;
    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
}

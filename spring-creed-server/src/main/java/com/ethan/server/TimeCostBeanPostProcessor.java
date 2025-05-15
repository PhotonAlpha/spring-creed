package com.ethan.server;

import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author EthanCao
 * @description spring creed
 * @date 23/4/24
 *
 * 监测 Spring boot 启动时间
 * 通过 BeanPostProcessor 原理，在前置处理时记录下当前时间，在后置处理时，用当前时间减去前置处理时间，就能知道每个 Bean 的初始化耗时。
 */
// @Component
public class TimeCostBeanPostProcessor implements BeanPostProcessor {
    private Map<String, Long> costMap = Maps.newConcurrentMap();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        costMap.put(beanName, System.currentTimeMillis());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (costMap.containsKey(beanName)) {
            Long start = costMap.get(beanName);
            long cost  = System.currentTimeMillis() - start;
            if (cost > 0) {
                costMap.put(beanName, cost);
                System.out.println("bean: " + beanName + "\ttime: " + cost);
            }
        }
        return bean;
    }
}

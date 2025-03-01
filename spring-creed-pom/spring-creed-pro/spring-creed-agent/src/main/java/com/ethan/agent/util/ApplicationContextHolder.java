package com.ethan.agent.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public ApplicationContextHolder() {
    }
    public ApplicationContextHolder(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (this) {
            if (ApplicationContextHolder.applicationContext == null) {
                ApplicationContextHolder.applicationContext = applicationContext;
            }
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
    public static <T> T getBean(String qualifier, Class<T> clazz) {
        return applicationContext.getBean(qualifier, clazz);
    }
}

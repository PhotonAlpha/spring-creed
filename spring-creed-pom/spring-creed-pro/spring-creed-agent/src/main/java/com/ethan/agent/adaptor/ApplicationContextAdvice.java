package com.ethan.agent.adaptor;

import com.ethan.agent.factory.CreedBuddyAgentBeanFactoryPostProcessor;
import com.ethan.agent.factory.CreedBuddyAgentBeanPostProcessor;
import com.ethan.agent.util.ApplicationContextHolder;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 20/11/24
 */
public class ApplicationContextAdvice {
    public static final Logger log = LoggerFactory.getLogger(ApplicationContextAdvice.class);
    @Advice.OnMethodEnter // 在方法返回时执行
    public static void intercept(@Advice.This Object applicationContext) {
        // log.info("ApplicationContextAdvice:{}", applicationContext.getClass().getName());
        if (applicationContext instanceof ConfigurableApplicationContext context) {
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            beanFactory.registerSingleton("creedBuddyAgentBeanPostProcessor", new CreedBuddyAgentBeanPostProcessor(context));
            beanFactory.registerSingleton("creedBuddyAgentBeanFactoryPostProcessor", new CreedBuddyAgentBeanFactoryPostProcessor(context));
            beanFactory.registerSingleton("applicationContextHolder", new ApplicationContextHolder(context));
            log.info("creedBuddyAgentBeanPostProcessor registered!");
        }
    }
}

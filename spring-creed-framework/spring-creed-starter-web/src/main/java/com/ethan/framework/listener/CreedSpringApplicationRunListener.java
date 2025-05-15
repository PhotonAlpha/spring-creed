/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.framework.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
public class CreedSpringApplicationRunListener implements SpringApplicationRunListener {
    //这个构造函数不能少，否则反射生成实例会报错
    public CreedSpringApplicationRunListener(SpringApplication application, String[] args) {
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.info("Listener:{} :{}",Thread.currentThread().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }
}

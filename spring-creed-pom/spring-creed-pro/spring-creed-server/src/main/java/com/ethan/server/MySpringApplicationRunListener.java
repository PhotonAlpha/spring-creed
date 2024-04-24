package com.ethan.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author EthanCao
 * @description spring creed
 * @date 23/4/24
 * 监测 Spring boot 启动时间
 */
@Slf4j
public class MySpringApplicationRunListener implements SpringApplicationRunListener {

    /**
     * 必须提供带 SpringApplication application, String[] args 参数的构造器，否则启动失败抛出异常.
     * @param application
     * @param args ：应用启动类上 mian 方法传入的参数
     */
    public MySpringApplicationRunListener(SpringApplication application, String[] args) {
        log.info(" MySpringApplicationRunListener 构造器执行...");
    }
    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        log.info("starting:: {}", LocalDateTime.now());
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        log.info("environmentPrepared:: {}", LocalDateTime.now());
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.info("contextPrepared:: {}", LocalDateTime.now());
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("contextLoaded:: {}", LocalDateTime.now());
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("started:: {}", LocalDateTime.now());
    }


    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("ready:: {}", LocalDateTime.now());
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.info("failed:: {}", LocalDateTime.now());
    }
}

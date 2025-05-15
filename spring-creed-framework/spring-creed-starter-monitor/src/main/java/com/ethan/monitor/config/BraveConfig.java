package com.ethan.monitor.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.swing.text.TabableView;
import java.util.Optional;

/**
 * Spring Boot Actuator provides dependency management and auto-configuration for Micrometer, an application metrics facade that supports numerous monitoring systems, including:
 * https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.supported.jetty
 * {@link org.springframework.boot.actuate.autoconfigure.tracing.BraveAutoConfiguration}
 *
 * Another fact is that the class annotated with @EnableAsync must be a @Configuration as well. Therefore start with an empty class:
 * https://stackoverflow.com/questions/56382562/async-not-working-for-method-having-return-type-void
 *
 * 在日志中添加trace ID，可以参考 Spring cloud sleuth 源码：https://github.com/spring-cloud/spring-cloud-sleuth/blob/76ad931b4298b18389d0a4ea739316c14682888b/spring-cloud-sleuth-autoconfigure/src/main/java/org/springframework/cloud/sleuth/autoconfig/TraceEnvironmentPostProcessor.java#L47-L48
 * 添加如下配置：
 * logging:
 *   pattern:
 *     level: "%5p [${spring.zipkin.service.name:${spring.application.name:}},%X{traceId:-},%X{spanId:-}]"
 *
 * https://stackoverflow.com/questions/77820591/spring-cloud-sleuth-to-micrometer-tracing-migration-sb-2-7-3-1-x-no-trace
 */
@Component
public class BraveConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Resource
    private Tracer tracer;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @PostConstruct
    public void init() {
        System.out.println("spanId:"+Optional.ofNullable(tracer).map(Tracer::currentSpan)
                .map(Span::context)
                .map(TraceContext::spanId)
                .orElse(""));
    }
}

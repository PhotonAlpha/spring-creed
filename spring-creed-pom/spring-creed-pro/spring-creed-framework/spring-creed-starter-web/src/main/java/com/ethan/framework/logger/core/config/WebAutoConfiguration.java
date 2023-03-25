/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.framework.logger.core.config;

import com.ethan.framework.operatelog.aop.OperateLogAspect;
import com.ethan.framework.operatelog.repository.OperateLogRepository;
import com.ethan.framework.operatelog.service.OperateLogFrameworkService;
import com.ethan.framework.operatelog.service.OperateLogFrameworkServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
// @EnableConfigurationProperties({WebProperties.class, XssProperties.class})
public class WebAutoConfiguration {
    @Bean
    public OperateLogAspect operateLogAspect() {
        return new OperateLogAspect();
    }

    @Bean
    public OperateLogFrameworkService operateLogFrameworkService(OperateLogRepository logRepository) {
        return new OperateLogFrameworkServiceImpl(logRepository);
    }
}

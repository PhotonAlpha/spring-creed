package com.ethan.common.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/2/24
 */
// @Configuration(proxyBeanMethods = false)
@Slf4j
public class HibernateConfig {
    @Bean
    public HibernatePropertiesCustomizer configureStatementInspector() {
        return properties -> properties.put(AvailableSettings.STATEMENT_INSPECTOR, new JpaInterceptor());
    }
}

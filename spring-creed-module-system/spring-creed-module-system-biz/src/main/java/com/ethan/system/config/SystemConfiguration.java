package com.ethan.system.config;

import com.ethan.infra.api.config.ConfigApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 9/10/24
 */
@Configuration(proxyBeanMethods = false)
public class SystemConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ConfigApi configApi() {
        return (key) -> "password";
    }
}

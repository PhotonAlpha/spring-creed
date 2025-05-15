package com.ethan.server.config;

import com.ethan.common.utils.SmReloadableResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 13/1/25
 */
@Configuration
public class MessageSourcesConfiguration {

    @Bean("resourcesMessageSource")
    public MessageSource resourcesMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new SmReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath*:resources-messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}

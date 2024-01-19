/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: caoqiang@dbs.com
 */

package com.ethan.system.pdf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * solve the Thymeleaf International lang switch issue in HTML
 * 解决前端显示i8n的问题，手动切换需要配置 new SessionLocaleResolver().setLocale(request, null, locale);
 */
@Configuration
public class LocalConfig {
    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }
}

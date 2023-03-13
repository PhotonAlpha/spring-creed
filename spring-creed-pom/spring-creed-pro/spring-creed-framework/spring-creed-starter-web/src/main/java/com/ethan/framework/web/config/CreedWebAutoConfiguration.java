/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.framework.web.config;

import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({WebProperties.class})
// @EnableConfigurationProperties({WebProperties.class, XssProperties.class})
public class CreedWebAutoConfiguration implements WebMvcConfigurer {
    @Resource
    private WebProperties webProperties;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurePathMat(configurer, webProperties.getAdminApi());
        configurePathMat(configurer, webProperties.getAppApi());
    }

    private void configurePathMat(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName())); // 仅仅匹配 controller 包
    }

/*     @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    } */

    /**
     * 解决跨域问题
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowedHeaders(CorsConfiguration.ALL)
                .exposedHeaders("Location")
                .allowCredentials(true);
    }

    private static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }

    /**
     * 创建 XssFilter Bean，解决 Xss 安全问题 TODO
     */
    // @Bean
    // public FilterRegistrationBean<XssFilter> xssFilter(XssProperties properties, PathMatcher pathMatcher) {
    //     return createFilterBean(new XssFilter(properties, pathMatcher), WebFilterOrderEnum.XSS_FILTER);
    // }
}

package com.ethan.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.stream.Stream;

/**
 * enabled CORS
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            //是否发送Cookie
            .allowCredentials(true)
            //放行哪些原始域
            .allowedOrigins("*")
            .allowedMethods(Stream.of(HttpMethod.values()).map(Enum::name).toArray(String[]::new))
            .allowedHeaders("*")
            //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
            //.exposedHeaders("*")
            .exposedHeaders("Content-Type", "X-Requested-With",
                "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")
        ;
    }
}

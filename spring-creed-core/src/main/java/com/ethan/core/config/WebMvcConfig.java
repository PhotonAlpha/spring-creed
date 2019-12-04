package com.ethan.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * setup the device
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Bean
  public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
    return new DeviceResolverHandlerInterceptor();
  }
  @Bean
  public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
    return new DeviceHandlerMethodArgumentResolver();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(deviceResolverHandlerInterceptor());
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(deviceHandlerMethodArgumentResolver());
  }
}

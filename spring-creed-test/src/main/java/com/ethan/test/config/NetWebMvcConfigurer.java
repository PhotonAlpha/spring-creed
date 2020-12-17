package com.ethan.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class NetWebMvcConfigurer implements WebMvcConfigurer {
  @Bean
  public ClientIpResolver clientIpResolver() {
    return new ClientIpResolver();
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(clientIpResolver());
  }
}

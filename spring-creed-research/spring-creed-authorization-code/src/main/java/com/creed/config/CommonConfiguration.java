package com.creed.config;

import com.creed.handler.TemplateResponseErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonConfiguration {
  @Bean
  public TemplateResponseErrorHandler responseErrorHandler() {
    return new TemplateResponseErrorHandler();
  }

  @Bean
  public RestTemplate restTemplate(){
    RestTemplate template = new RestTemplate();
    template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    template.setErrorHandler(responseErrorHandler());
    return template;
  }
}

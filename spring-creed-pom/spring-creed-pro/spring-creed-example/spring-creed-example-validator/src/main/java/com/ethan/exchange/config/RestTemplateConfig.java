package com.ethan.exchange.config;

import com.ethan.exchange.client.ArtisanClient;
import com.ethan.exchange.client.ArtisanClientX;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * {@see https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface-method-parameters }
 * @description spring-creed-pro
 * @date 10/3/25
 */
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public ArtisanClient artisanClient(RestTemplate restTemplate) {
        // restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080"));
        // restTemplate.setDefaultUriVariables();
        RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ArtisanClient.class);
    }
    @Bean
    public ArtisanClientX artisanClientX(RestTemplate restTemplate) {
        RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ArtisanClientX.class);
    }

}

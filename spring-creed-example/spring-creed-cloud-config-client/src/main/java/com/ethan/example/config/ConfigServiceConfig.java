package com.ethan.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 13/5/25
 */
@Configuration
@Slf4j
@LoadBalancerClients(value = {
        @LoadBalancerClient(value = "remote-cluster", configuration = MyLoadBalancerConfiguration.class)
})
public class ConfigServiceConfig {
    @Bean("remoteClusterRestTemplate")
    @LoadBalanced
    public RestTemplate remoteClusterRestTemplate() {
        log.info("loadbalancer restTemplate");
        return new RestTemplate();
    }
    @Bean("healthCheckRestTemplate")
    public RestTemplate healthCheckRestTemplate() {
        log.info("healthCheck restTemplate");
        return new RestTemplate();
    }


    /* @Bean
    public ConfigServicePropertySourceLocator configServicePropertySource(ConfigClientProperties properties, @Qualifier("configServerLb") RestTemplate restTemplate) {
        log.info("configServicePropertySource");
        ConfigServicePropertySourceLocator sourceLocator = new ConfigServicePropertySourceLocator(properties);
        sourceLocator.setRestTemplate(restTemplate);
        return sourceLocator;
    } */
}

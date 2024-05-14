package com.ethan.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 1/4/24
 */
@Configuration
public class GatewayConfiguration {
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        // return builder.routes().build();
        return builder.routes()
                .route(p -> p
                        .path("/spring-cloud-gateway-actuator/actuator")
                        .filters(f -> f.addRequestHeader("Hello", "World"))
                        .uri("http://127.0.0.1:8080"))
                .route(p -> p
                        .path("/spring-cloud-gateway-actuator/showAll")
                        .uri("http://127.0.0.1:8080"))
                .build();
    }

}

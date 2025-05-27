package com.ethan.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.HealthCheckServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplierBuilder;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

/**
 * {@link LoadBalancerInterceptor}
 * {@link BlockingLoadBalancerClient#getSupportedLifecycleProcessors(String)}
 * @author EthanCao
 * @description spring-creed
 * @date 13/5/25
 */
@Slf4j
public class MyLoadBalancerConfiguration {
    private static final String SLASH = "/";
    @Bean //默认可用配置
    public ServiceInstanceListSupplier discoveryClientWithHealthChecksServiceInstanceListSupplier(
            ConfigurableApplicationContext context, @Qualifier("healthCheckRestTemplate") RestTemplate restTemplate) {
        log.info("initializing ServiceInstanceListSupplier");
        ServiceInstanceListSupplierBuilder.DelegateCreator healthCheckCreator = (appContext, delegate) -> {
            LoadBalancerClientFactory loadBalancerClientFactory = appContext.getBean(LoadBalancerClientFactory.class);
            return blockingHealthCheckServiceInstanceListSupplier(restTemplate, delegate, loadBalancerClientFactory);
        };

        ServiceInstanceListSupplier supplier = ServiceInstanceListSupplier.builder()
                // .withHints()
                .withBlockingDiscoveryClient()
                .with(healthCheckCreator)
                .build(context);
// supplier.get()
        return supplier;
    }

    private ServiceInstanceListSupplier blockingHealthCheckServiceInstanceListSupplier(RestTemplate restTemplate,
                                                                                       ServiceInstanceListSupplier delegate,
                                                                                       ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
        return new HealthCheckServiceInstanceListSupplier(delegate, loadBalancerClientFactory,
                (serviceInstance, healthCheckPath) -> Mono.defer( () -> {
                    URI uri = UriComponentsBuilder.fromUriString(getUri(serviceInstance, healthCheckPath)).build()
                            .toUri();
                    try {
                        return Mono
                                .just(HttpStatus.OK.equals(restTemplate.getForEntity(uri, Void.class).getStatusCode()));

                    }
                    catch (Exception ignored) {
                        log.error("getForEntity error:{}", ExceptionUtils.getMessage(ignored));
                        // 自定义health check
                        return Mono.just(false);
                    }
                }).doOnNext(res -> log.info("Service Instance {} is alive:{}", serviceInstance, res)));
    }

    static String getUri(ServiceInstance serviceInstance, String healthCheckPath) {
        if (StringUtils.hasText(healthCheckPath)) {
            String path = healthCheckPath.startsWith(SLASH) ? healthCheckPath : SLASH + healthCheckPath;
            return serviceInstance.getUri().toString() + path;
        }
        return serviceInstance.getUri().toString();
    }
}

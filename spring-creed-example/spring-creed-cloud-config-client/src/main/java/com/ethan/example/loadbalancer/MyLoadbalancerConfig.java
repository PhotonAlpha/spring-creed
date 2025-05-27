package com.ethan.example.loadbalancer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.stats.MicrometerStatsLoadBalancerLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 15/5/25
 */
@Configuration
public class MyLoadbalancerConfig {
    // @Bean
    public MeterRegistry meterRegistry() {
        return new LoggingMeterRegistry();
    }


    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    public MicrometerStatsLoadBalancerLifecycle micrometerStatsLifecycle(MeterRegistry meterRegistry,
                                                                         ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {

        // LoadBalancerLifecycle<Object, Object, ServiceInstance>
        return new MicrometerStatsLoadBalancerLifecycle(meterRegistry, loadBalancerFactory);
    }
}

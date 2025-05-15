package com.ethan.example.config;

import com.ethan.example.aop.CacheAnnotationAdvisor;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 1/4/25
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching(order = Ordered.LOWEST_PRECEDENCE - 1)
@Slf4j
public class CacheConfig {
    @Bean
    public MyRemovalListener removalListener() {
        return new MyRemovalListener();
    }

    @Bean
    public CacheManager cacheManager(CachePropertiesBean cachePropertiesBean, MyRemovalListener removalListener) {

        /*
         * to solve the issue: No Caffeine AsyncCache available
         * ref: https://github.com/spring-projects/spring-framework/issues/31861
         */
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        for (var bean : cachePropertiesBean.getConfigs()) {
            log.info("Building cache name: {}, cache expiry: {}", bean.getCacheName(), bean.getExpireAfterWriteMins());
            AsyncCache<Object, Object> cache = Caffeine.newBuilder()
                    .initialCapacity(bean.getInitialCapacity())
                    .maximumSize(bean.getMaximumSize())
                    .expireAfterWrite(bean.getExpireAfterWriteMins(), TimeUnit.SECONDS)
                    // .weakValues()
                    .removalListener(removalListener)
                    .scheduler(Scheduler.systemScheduler())
                    .recordStats()
                    .buildAsync();
            cacheManager.registerCustomCache(bean.getCacheName(), cache);
        }


        return cacheManager;
    }

    @Bean
    public CacheAnnotationAdvisor cacheAnnotationAdvisor(CacheOperationSource cacheOperationSource) {
        return new CacheAnnotationAdvisor(cacheOperationSource);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

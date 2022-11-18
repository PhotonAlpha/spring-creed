/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.apm.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/1/2022 5:31 PM
 */
@Configuration
@EnableCaching
public class CacheConfig {
    //----cache configuration
    @Value("${creed.micrometer.cap:200}")
    private int initialCapacity;
    @Value("${creed.micrometer.maxsize:10000}")
    private long maximumSize;
    @Value("${creed.micrometer.duration:72000}")
    private long duration;
    @Bean
    public Caffeine caffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(duration, TimeUnit.SECONDS)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager(Caffeine caffeine) {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(caffeine);


        Cache<Object, Object> customCache = Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(20)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
        caffeineCacheManager.registerCustomCache("CUSTOM_CACHE", customCache);

        return caffeineCacheManager;
    }
}

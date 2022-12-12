package com.ethan.e2e.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {
  //----cache configuration
  @Value("${creed.micrometer.cap:200}")
  private int initialCapacity;
  @Value("${creed.micrometer.maxsize:10000}")
  private long maximumSize;
  @Value("${creed.micrometer.duration:72000}")
  private long duration;
  @Bean
  public CacheManager cacheManager(CaffeineCacheProperty caffeineCacheProperty) {
    CaffeineCacheManager manager = new CaffeineCacheManager();

    @NonNull Caffeine<Object, Object> defaultCache = Caffeine.newBuilder()
            .initialCapacity(initialCapacity)
            .maximumSize(maximumSize)
            .expireAfterWrite(duration, TimeUnit.SECONDS)
            .recordStats();
    manager.setCaffeine(defaultCache);
    for (CaffeineCacheProperty.CaffeineConfig bean : caffeineCacheProperty.getConfigs()) {
        @NonNull Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(bean.getInitialCapacity())
            .maximumSize(bean.getMaximumSize())
            .expireAfterWrite(bean.getExpireAfterWriteMins(), TimeUnit.MINUTES)
            .build();
      manager.registerCustomCache(bean.getCacheName(), cache);
    }
    // List<CaffeineCache> caches = caffeineCacheProperty.getConfigs().stream().map(bean -> {
    //   @NonNull Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(bean.getInitialCapacity())
    //       .maximumSize(bean.getMaximumSize())
    //       .expireAfterWrite(bean.getExpireAfterWriteMins(), TimeUnit.MINUTES)
    //       .build();
    //   return new CaffeineCache(bean.getCacheName(), cache);
    // }).collect(Collectors.toList());
    // SimpleCacheManager scm = new SimpleCacheManager();
    // scm.setCaches(caches);
    return manager;
  }
}

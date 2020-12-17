package com.ethan.e2e.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class CacheConfig {
  @Bean
  public CacheManager cacheManager(CaffeineCacheProperty caffeineCacheProperty) {
    List<CaffeineCache> caches = caffeineCacheProperty.getConfigs().stream().map(bean -> {
      @NonNull Cache<Object, Object> cache = Caffeine.newBuilder().initialCapacity(bean.getInitialCapacity())
          .maximumSize(bean.getMaximumSize())
          .expireAfterWrite(bean.getExpireAfterWriteMins(), TimeUnit.MINUTES)
          .build();
      return new CaffeineCache(bean.getCacheName(), cache);
    }).collect(Collectors.toList());
    SimpleCacheManager scm = new SimpleCacheManager();
    scm.setCaches(caches);
    return scm;
  }
}

package com.ethan.cache.config;

import com.ethan.cache.model.CaffeineCacheBean;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//@Configuration
@Deprecated
public class CaffeineConfig {
  private final ApplicationContext applicationContext;
  private final CacheProperties cacheProperties;

  public CaffeineConfig(ApplicationContext applicationContext, CacheProperties cacheProperties) {
    this.applicationContext = applicationContext;
    this.cacheProperties = cacheProperties;
  }
  @Bean
  public CacheRemovalListener cacheRemovalListener() {
    return new CacheRemovalListener();
  }

  /**
   * system local cache configuration
   * //TODO
   * create schedule pool executor to refresh the cache
   */
  @Bean
  public CacheManager cacheManager(CacheRemovalListener cacheRemovalListener) {
    List<CaffeineCache> caches = cacheProperties.getConfig().stream().map(bean -> {
      Cache<Object, Object> cache = Caffeine.newBuilder()
          .initialCapacity(bean.getCaffeine().getInitialCapacity())
          .maximumSize(bean.getCaffeine().getMaximumSize())
          .expireAfterWrite(bean.getCaffeine().getExpireAfterWriteMins(), TimeUnit.SECONDS)
          //.weakKeys()
           .weakValues()
          .removalListener(cacheRemovalListener)
          .recordStats()
          .build();
      return new CaffeineCache(bean.getCacheName(), cache);
    }).collect(Collectors.toList());
    SimpleCacheManager manager = new SimpleCacheManager();
    manager.setCaches(caches);
    return manager;
  }
  @Slf4j
  static class CacheRemovalListener implements RemovalListener<Object, Object> {
    @Override
    public void onRemoval(@Nullable Object o, @Nullable Object o2, @NonNull RemovalCause removalCause) {
      log.info("CacheRemovalListener:{} :{}", new Object[]{o, removalCause});
    }
  }
}

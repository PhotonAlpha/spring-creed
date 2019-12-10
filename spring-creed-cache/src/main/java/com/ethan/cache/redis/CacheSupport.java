package com.ethan.cache.redis;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * cache register and refresh cache
 */
public interface CacheSupport {
  /**
   * register cache method
   * @param invokedBean proxy bean name
   * @param invokedMethod proxy bean method
   * @param invocationParamTypes proxy bean parameter types
   * @param invocationArgs  proxy bean parameters
   * @param cacheNames  cache namespace(annotation {@link org.springframework.cache.annotation.Cacheable} value)
   * @param cacheKey cache key(annotation {@link org.springframework.cache.annotation.Cacheable} key)
   */
  void registerInvocation(Object invokedBean, Method invokedMethod, @SuppressWarnings("rawtypes") Class[] invocationParamTypes, Object[] invocationArgs, Set<String> cacheNames, String cacheKey);

  /**
   * update the cache values as the cacheName and the cacheKey
   * @param cacheName cache name space
   * @param cacheKey cache key
   */
  void refreshCacheByKey(String cacheName, String cacheKey);
}

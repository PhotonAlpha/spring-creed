package com.ethan.cache.redis;

import com.ethan.cache.LayeringCache;
import com.ethan.cache.redis.expression.CacheOperationExpressionEvaluator;
import com.ethan.context.utils.ReflectionUtils;
import com.ethan.context.utils.SpringContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * refresh cache manually
 */
@Component
public class CacheSupportImpl implements CacheSupport {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(CacheSupportImpl.class);
	private final CacheOperationExpressionEvaluator evaluator = new CacheOperationExpressionEvaluator();

  private static final String SEPARATOR = "#";

  @Autowired
  private KeyGenerator keyGenerator;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private RedisOperations<String, Object> redisOperations;

  @Override
  public void registerInvocation(Object invokedBean, Method invokedMethod, Class[] invocationParamTypes,
                                 Object[] invocationArgs, Set<String> cacheNames, String cacheKey) {
    // Get the actual value on the annotation
    Collection<? extends Cache> caches = getCache(cacheNames);

    // Get the SpEL value of the key
    Object key = generateKey(caches, cacheKey, invokedMethod, invocationArgs, invokedBean, CacheOperationExpressionEvaluator.NO_RESULT);

    final CachedMethodInvocation invocation = new CachedMethodInvocation(key, invokedBean, invokedMethod, invocationParamTypes, invocationArgs);
    for (Cache cache : caches) {
      if (cache instanceof LayeringCache) {
        CustomizedRedisCache redisCache = getRedisCache(cache);
        // check whether is force refresh
        if (redisCache != null && redisCache.isForceRefresh()) {
          log.info("registerInvocation-->:{}", key);
          redisOperations.opsForValue().set(getInvocationCacheKey(redisCache.getCacheKey(key)), invocation, redisCache.getExpirationTime(), TimeUnit.SECONDS);
        }
      }
    }
  }

  @Override
  public void refreshCacheByKey(String cacheName, String cacheKey) {
    Object result = redisOperations.opsForValue().get(getInvocationCacheKey(cacheKey));
    if (result instanceof CachedMethodInvocation) {
      CachedMethodInvocation invocation = (CachedMethodInvocation) result;
      // execute refresh
      refreshCache(invocation, cacheName);
    } else {
      log.error("Refresh the redis cache, the deserialization method information is abnormal");
    }
  }

  private void refreshCache(CachedMethodInvocation invocation, String cacheName) {
    try {
      // Call the method through the proxy and record the return value
      Object computed = invoke(invocation);

      // Get the cache object that operates the cache through cacheManager
      Cache cache = cacheManager.getCache(cacheName);
      // Update the cache through the Cache object
      cache.put(invocation.getKey(), computed);

      CustomizedRedisCache redisCache = getRedisCache(cache);
      if (redisCache != null) {
        long expireTime = redisCache.getExpirationTime();
        // Refresh the valid time of the cache method information key in redis
        redisOperations.expire(getInvocationCacheKey(redisCache.getCacheKey(invocation.getKey())), expireTime, TimeUnit.SECONDS);

        log.info("cache：{}-{}, Reload data", cacheName, invocation.getKey());
      }
    } catch (Exception e) {
      log.info("refresh cache failure:{} exception:{}", e.getMessage(), e);
    }

  }

  /**
   * 通过cache名称获取cache列表
   *
   * @param annotatedCacheNames
   * @return
   */
  public Collection<Cache> getCache(Set<String> annotatedCacheNames) {

    Collection<String> cacheNames = generateValue(annotatedCacheNames);

    if (CollectionUtils.isEmpty(cacheNames)) {
      return Collections.emptyList();
    } else {
      Collection<Cache> result = new ArrayList<>();
      for (String cacheName : cacheNames) {
        Cache cache = this.cacheManager.getCache(cacheName);
        if (cache == null) {
          throw new IllegalArgumentException("Cannot find cache named '" + cacheName + "' for ");
        }
        result.add(cache);
      }
      return result;
    }
  }

  /**
   * Get the value of the value attribute on the annotation (cacheNames)
   * @param annotatedCacheNames
   * @return
   */
  private Collection<String> generateValue(Set<String> annotatedCacheNames) {
    Collection<String> cacheNames = new HashSet<>();
    for (final String cacheName : annotatedCacheNames) {
      String[] cacheParams = cacheName.split(SEPARATOR);
      // Intercept the name to get the real value
      String realCacheName = cacheParams[0];
      cacheNames.add(realCacheName);
    }
    return cacheNames;
  }

  /**
   * 解析SpEL表达式，获取注解上的key属性值
   * 直接扣的Spring解析表达式部分代码
   *
   * @return
   */
  protected Object generateKey(Collection<? extends Cache> caches, String key, Method method, Object[] args,
                               Object target, Object result) {

    // Get the value of the key attribute on the annotation
    Class<?> targetClass = getTargetClass(target);
    if (org.springframework.util.StringUtils.hasText(key)) {
      EvaluationContext evaluationContext = evaluator.createEvaluationContext(caches, method, args, target,
          targetClass, result, null);

      AnnotatedElementKey methodCacheKey = new AnnotatedElementKey(method, targetClass);
      return evaluator.key(key, methodCacheKey, evaluationContext);
    }
    return this.keyGenerator.generate(target, method, args);
  }

  /**
   * get the class information
   *
   * @param target
   * @return
   */
  private Class<?> getTargetClass(Object target) {
    return AopProxyUtils.ultimateTargetClass(target);
  }

  private CustomizedRedisCache getRedisCache(Cache cache) {
    LayeringCache layeringCache = ((LayeringCache) cache);
    return layeringCache.getSecondaryCache();
  }

  private String getInvocationCacheKey(String cacheKey) {
    return cacheKey + CustomizedRedisCache.INVOCATION_CACHE_KEY_SUFFIX;
  }

  private Object invoke(CachedMethodInvocation invocation) throws Exception {

    // 获取执行方法所需要的参数
    Object[] args = null;
    if (!CollectionUtils.isEmpty(invocation.getArguments())) {
      args = invocation.getArguments().toArray();
    }
    // 通过先获取Spring的代理对象，在根据这个对象获取真实的实例对象
    Object target = ReflectionUtils.getTarget(SpringContextUtils.getBean(Class.forName(invocation.getTargetBean())));

    final MethodInvoker invoker = new MethodInvoker();
    invoker.setTargetObject(target);
    invoker.setArguments(args);
    invoker.setTargetMethod(invocation.getTargetMethod());
    invoker.prepare();

    return invoker.invoke();
  }
}

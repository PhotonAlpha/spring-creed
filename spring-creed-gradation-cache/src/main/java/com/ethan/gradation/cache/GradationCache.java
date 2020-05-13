package com.ethan.gradation.cache;

import com.ethan.gradation.cache.caffeine.CaffeineCacheGdt;
import com.ethan.gradation.cache.redis.RedisCacheGdt;
import com.ethan.gradation.config.GradationCacheProperty;
import com.ethan.gradation.listener.RedisPubSubMessage;
import com.ethan.gradation.listener.RedisPubSubMessageType;
import com.ethan.gradation.listener.RedisPublisher;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class GradationCache extends AbstractValueAdaptingCache implements CacheStatsManager {
  /**
   * redis 客户端
   */
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * 一级缓存
   */
  private CaffeineCacheGdt firstCache;

  /**
   * 二级缓存
   */
  private RedisCacheGdt secondCache;

  /**
   * 是否使用一级缓存， 默认true
   */
  private boolean useFirstCache;
  private String name;
  private GradationCacheProperty gradationCacheProperty;

  public GradationCache(String name, boolean allowNullValues, RedisTemplate<String, Object> redisTemplate, CaffeineCacheGdt firstCache, RedisCacheGdt secondCache, boolean useFirstCache) {
    super(allowNullValues);
    Assert.notNull(name, "Name must not be null!");
    if (useFirstCache) {
      Assert.notNull(firstCache, " if enable useFirstCache, the FirstCache must not be null!");
    }
    this.name = name;
    this.redisTemplate = redisTemplate;
    this.firstCache = firstCache;
    this.secondCache = secondCache;
    this.useFirstCache = useFirstCache;
  }



  @Override
  protected Object lookup(Object key) {
    Object result = null;
    if (useFirstCache) {
      result = fromValueWrapperValue(firstCache.get(key));
      log.info("查询一级缓存。 key={},返回值是:{}", key, result);
    }
    if (result == null) {
      result = fromValueWrapperValue(secondCache.get(key));
      if (useFirstCache) {
        firstCache.putIfAbsent(key, result);
        log.info("查询二级缓存,并将数据放到一级缓存。 key={},返回值是:{}", key, result);
      }
    }
    return result;
  }

  protected Object fromValueWrapperValue(@Nullable Object wrapperValue) {
    if (isAllowNullValues() && wrapperValue == NullValue.INSTANCE) {
      return null;
    } else if (wrapperValue instanceof ValueWrapper) {
      return ((ValueWrapper) wrapperValue).get();
    }
    return wrapperValue;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Object getNativeCache() {
    return this;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    if (useFirstCache) {
      Object result = fromValueWrapperValue(firstCache.get(key));
      log.info("查询一级缓存。 key={},返回值是:{}", key, result);
      if (result != null) {
        return (T) fromStoreValue(result);
      }
    }

    T result = secondCache.get(key, valueLoader);
    if (useFirstCache) {
      firstCache.putIfAbsent(key, result);
      log.info("查询二级缓存,并将数据放到一级缓存。 key={},返回值是:{}", key, result);
    }
    return (T) fromStoreValue(result);
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    if (useFirstCache) {
      Object result = firstCache.get(key, type);
      log.info("查询一级缓存。 key={},返回值是:{}", key, result);
      if (result != null) {
        return (T) fromStoreValue(result);
      }
    }

    T result = secondCache.get(key, type);
    if (useFirstCache) {
      firstCache.putIfAbsent(key, result);
      log.info("查询二级缓存,并将数据放到一级缓存。 key={},返回值是:{}", key, result);
    }
    return (T) fromStoreValue(result);
  }

  @Override
  public void put(Object key, Object value) {
    secondCache.put(key, value);
    // 删除一级缓存
    if (useFirstCache) {
      deleteFirstCache(key);
    }
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    Object result = secondCache.putIfAbsent(key, value);
    // 删除一级缓存
    if (useFirstCache) {
      deleteFirstCache(key);
    }
    return toValueWrapper(result);
  }

  @Override
  public void evict(Object key) {
    // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
    secondCache.evict(key);
    // 删除一级缓存
    if (useFirstCache) {
      deleteFirstCache(key);
    }
  }

  @Override
  public void clear() {
    // 删除的时候要先删除二级缓存再删除一级缓存，否则有并发问题
    secondCache.clear();
    if (useFirstCache) {
      // 清除一级缓存需要用到redis的订阅/发布模式，否则集群中其他服服务器节点的一级缓存数据无法删除
      RedisPubSubMessage message = new RedisPubSubMessage();
      message.setCacheName(getName());
      message.setMessageType(RedisPubSubMessageType.CLEAR);
      // 发布消息
      RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
    }
  }

  private void deleteFirstCache(Object key) {
    // 删除一级缓存需要用到redis的Pub/Sub（订阅/发布）模式，否则集群中其他服服务器节点的一级缓存数据无法删除
    RedisPubSubMessage message = new RedisPubSubMessage();
    message.setCacheName(getName());
    message.setKey(key);
    message.setMessageType(RedisPubSubMessageType.EVICT);
    // 发布消息
    RedisPublisher.publisher(redisTemplate, new ChannelTopic(getName()), message);
  }

  @Override
  public CacheStats getCacheStats() {
    CacheStats cacheStats = CacheStats.empty() ;
    if (useFirstCache && Objects.nonNull(firstCache) && firstCache instanceof CaffeineCacheGdt) {
      CacheStats stats = firstCache.getCacheStats();
      cacheStats.plus(stats);
    }

    if (Objects.nonNull(secondCache) && secondCache instanceof RedisCacheGdt) {
      CacheStats stats = secondCache.getCacheStats();
      cacheStats.plus(stats);
    }
    return cacheStats;
  }

  public CaffeineCacheGdt getFirstCache() {
    return firstCache;
  }

  public void setFirstCache(CaffeineCacheGdt firstCache) {
    this.firstCache = firstCache;
  }

  public RedisCacheGdt getSecondCache() {
    return secondCache;
  }

  public void setSecondCache(RedisCacheGdt secondCache) {
    this.secondCache = secondCache;
  }

  public GradationCacheProperty getGradationCacheProperty() {
    return gradationCacheProperty;
  }

  public void setGradationCacheProperty(GradationCacheProperty gradationCacheProperty) {
    this.gradationCacheProperty = gradationCacheProperty;
  }
}

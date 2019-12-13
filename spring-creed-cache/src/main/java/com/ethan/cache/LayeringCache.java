package com.ethan.cache;

import com.ethan.cache.constants.CacheConstant;
import com.ethan.cache.constants.ChannelTopicEnum;
import com.ethan.cache.listener.RedisPublisher;
import com.ethan.cache.redis.CustomizedRedisCache;
import com.ethan.context.utils.InstanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public class LayeringCache extends AbstractValueAdaptingCache {
  /**
   * cache name
   */
  private final String name;

  private boolean enablePrimaryCache = true;
  /**
   * redis cache
   */
  private final CustomizedRedisCache redisCache;

  private final CaffeineCache caffeineCache;

  RedisOperations<? extends Object, ? extends Object> redisOperations;

  private final RedisConnectionFactory connectionFactory;

  /**
   *
   * @param allowNullValues allow nullable, default is false
   * @param prefix
   * @param redisOperations
   * @param expiration  redis expire time
   * @param preloadSecondTime redis auto refresh idle time
   * @param name  cache name
   * @param enablePrimaryCache enable the caffeine cache, default false
   * @param forceRefresh  force refresh(reload from database)
   * @param caffeineCache caffeine cache
   */
  public LayeringCache(boolean allowNullValues,String prefix, RedisOperations<? extends Object, ? extends Object> redisOperations, RedisConnectionFactory connectionFactory,
                       long expiration, long preloadSecondTime, String name, boolean enablePrimaryCache,boolean forceRefresh,
                       com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache) {
    super(allowNullValues);
    this.name = name;
    this.enablePrimaryCache = enablePrimaryCache;
    this.redisOperations = redisOperations;
    this.connectionFactory = connectionFactory;
    this.redisCache = new CustomizedRedisCache(name, RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
        getRedisCacheConfigurationWithTtl(), prefix, redisOperations, expiration, preloadSecondTime, forceRefresh);
    this.caffeineCache = new CaffeineCache(name, caffeineCache, allowNullValues);
  }

  private RedisCacheConfiguration getRedisCacheConfigurationWithTtl() {
    RedisSerializationContext.SerializationPair<String> keySerializer = RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
    RedisSerializationContext.SerializationPair<Object> valueSerializer =
        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(InstanceUtils.getMapperInstance()));

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(keySerializer)
        .serializeValuesWith(valueSerializer);
  }

  @Override
  protected Object lookup(Object key) {
    Object value = null;
    if (enablePrimaryCache) {
      value = caffeineCache.get(key);
      log.debug("search the L1 cache key:{}, value:{}", key, value);
    }
    if (value == null) {
      value = redisCache.get(key);
      log.debug("search the L2 cache key:{}, value:{}", key, value);
    }
    return value;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Object getNativeCache() {
    return this;
  }

  public CaffeineCache getPrimaryCache() {
    return this.caffeineCache;
  }
  public RedisCache getSecondaryCache() {
    return this.redisCache;
  }

  @Override
  public ValueWrapper get(Object key) {
    ValueWrapper wrapper = null;
    if (enablePrimaryCache) {
      wrapper = caffeineCache.get(key);
      log.debug("search the level 1 cache key:{}, value:{}", key, wrapper);
    }
    if (wrapper == null) {
      wrapper = redisCache.get(key);
      caffeineCache.put(key, wrapper == null ? null : wrapper.get());
      // query L2 cache, and update L1 cache
      log.debug("search the level 2 cache key:{}, value:{}", key, wrapper);
    }
    return wrapper;
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    T value = null;
    if (enablePrimaryCache) {
      value = caffeineCache.get(key, type);
      log.debug("search the level 1 cache key:{}, value:{}", key, value);
    }
    if (value == null) {
      value = redisCache.get(key, type);
      caffeineCache.put(key, value);
      log.debug("search the level 2 cache key:{}, value:{}", key, value);
    }
    return value;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    T value;
    if (enablePrimaryCache) {
      value = (T) caffeineCache.getNativeCache().get(key, k -> getForSecondaryCache(k, valueLoader));
    } else {
      value = (T) getForSecondaryCache(key, valueLoader);
    }

    if (value instanceof NullValue) {
      return null;
    }
    return value;
  }

  @Override
  public void put(Object key, Object value) {
    if (enablePrimaryCache) {
      caffeineCache.put(key, value);
    }
    redisCache.put(key, value);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    if (enablePrimaryCache) {
      caffeineCache.putIfAbsent(key, value);
    }
    return redisCache.putIfAbsent(key, value);
  }

  @Override
  public void evict(Object key) {
    // When deleting, you must first delete the L2 cache and then delete the L1 cache, otherwise there will be concurrency issues.
    redisCache.evict(key);
    if (enablePrimaryCache) {
      // Deleting the primary cache requires the Pub / Sub (subscribe / publish) mode of redis,
      // otherwise the primary cache data of other server nodes in the cluster cannot be deleted
      Map<String, Object> message = new HashMap<>();
      message.put(CacheConstant.CACHE_NAME, name);
      message.put(CacheConstant.CACHE_KEY, key);
      RedisPublisher redisPublisher = new RedisPublisher(redisOperations, ChannelTopicEnum.REDIS_CACHE_DELETE_TOPIC.getChannelTopic());
      redisPublisher.publisher(message);
    }
  }

  @Override
  public void clear() {
    redisCache.clear();
    if (enablePrimaryCache) {
      Map<String, Object> message = new HashMap<>();
      message.put(CacheConstant.CACHE_NAME, name);
      RedisPublisher redisPublisher = new RedisPublisher(redisOperations, ChannelTopicEnum.REDIS_CACHE_CLEAR_TOPIC.getChannelTopic());
      redisPublisher.publisher(message);
    }
  }



  private <T> Object getForSecondaryCache(Object key, Callable<T> valueLoader) {
    T value = redisCache.get(key, valueLoader);
    log.debug("search the level 2 cache key:{}, value:{}", key, value);
    return toStoreValue(value);
  }
}

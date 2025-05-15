package com.ethan.cache;

import com.ethan.cache.constants.CacheConstant;
import com.ethan.cache.constants.ChannelTopicEnum;
import com.ethan.cache.listener.RedisPublisher;
import com.ethan.cache.model.RedisCacheBean;
import com.ethan.cache.redis.CustomizedRedisCache;
import com.ethan.common.utils.json.JacksonUtils;
import org.slf4j.Logger;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class LayeringCache extends AbstractValueAdaptingCache {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(LayeringCache.class);
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

  private final RedisOperations<? extends Object, ? extends Object> redisOperations;

  /**
   *
   * @param allowNullValues allow nullable, default is false
   * @param prefix  cache key prefix
   * @param redisOperations
   * @param connectionFactory
   * @param name cache name
   * @param redisCacheBean redis cache configuration
   * @param caffeineCache caffeine cache
   */
  public LayeringCache(boolean allowNullValues,String prefix, RedisOperations<? extends Object, ? extends Object> redisOperations, RedisConnectionFactory connectionFactory,
                       String name, RedisCacheBean redisCacheBean,
                       com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache) {
    super(allowNullValues);
    this.name = name;
    this.enablePrimaryCache = redisCacheBean.getEnablePrimaryCache();
    this.redisOperations = redisOperations;
    this.redisCache = new CustomizedRedisCache(name, RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
        getRedisCacheConfigurationWithTtl(redisCacheBean.getExpirationTime(), prefix), redisOperations, redisCacheBean);
    this.caffeineCache = new CaffeineCache(name, caffeineCache, allowNullValues);
  }

  private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(long seconds, String prefix) {
    RedisSerializationContext.SerializationPair<String> keySerializer = RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
    RedisSerializationContext.SerializationPair<Object> valueSerializer =
        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(JacksonUtils.objectMapper()));

    return RedisCacheConfiguration.defaultCacheConfig()
        .prefixCacheNameWith(prefix)
        .entryTtl(Duration.ofSeconds(seconds))
        .serializeKeysWith(keySerializer)
        .serializeValuesWith(valueSerializer);
  }

  @Override
  protected Object lookup(Object key) {
    Object value = null;
    if (enablePrimaryCache) {
      value = caffeineCache.get(key);
      log.info("search the L1 cache key:{}, value:{}", key, value);
    }
    if (value == null) {
      value = redisCache.get(key);
      log.info("search the L2 cache key:{}, value:{}", key, value);
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
  public CustomizedRedisCache getSecondaryCache() {
    return this.redisCache;
  }

  @Override
  public ValueWrapper get(Object key) {
    ValueWrapper wrapper = null;
    if (enablePrimaryCache) {
      wrapper = caffeineCache.get(key);
      log.info("search the level 1 cache key:{}, value:{}", key, wrapper);
    }
    if (wrapper == null) {
      wrapper = redisCache.get(key);
      caffeineCache.put(key, wrapper == null ? null : wrapper.get());
      // query L2 cache, and update L1 cache
      log.info("search the level 2 cache key:{}, value:{}", key, wrapper);
    }
    return wrapper;
  }

  @Override
  public <T> T get(Object key, Class<T> type) {
    T value = null;
    if (enablePrimaryCache) {
      value = caffeineCache.get(key, type);
      log.info("search the level 1 cache key:{}, value:{}", key, value);
    }
    if (value == null) {
      value = redisCache.get(key, type);
      caffeineCache.put(key, value);
      log.info("search the level 2 cache key:{}, value:{}", key, value);
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
    log.info("search the level 2 cache key:{}, value:{}", key, value);
    return toStoreValue(value);
  }
}

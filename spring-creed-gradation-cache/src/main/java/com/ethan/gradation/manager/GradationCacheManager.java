package com.ethan.gradation.manager;

import com.ethan.gradation.cache.GradationCache;
import com.ethan.gradation.cache.caffeine.CaffeineCacheGdt;
import com.ethan.gradation.cache.redis.RedisCacheGdt;
import com.ethan.gradation.config.CaffeineCacheProperty;
import com.ethan.gradation.config.GradationCacheProperty;
import com.ethan.gradation.config.RedisCacheProperty;
import com.ethan.gradation.constant.ExpireMode;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GradationCacheManager extends AbstractCacheManagerGdt {
  private RedisTemplate<String, Object> redisTemplate;
  private RemovalListener<Object, Object> removalListener;
  private Map<String, GradationCacheProperty> initialCacheConfiguration = new LinkedHashMap<>();
  private static final RedisSerializer<String> DEFAULT_KEY_SERIALIZATION_PAIR = RedisSerializer.string();
  private static final RedisSerializer<Object> DEFAULT_VALUE_SERIALIZATION_PAIR = new GenericJackson2JsonRedisSerializer();

  // default config
  private RedisSerializer<String> keySerializationPair = DEFAULT_KEY_SERIALIZATION_PAIR;
  private RedisSerializer<Object> valueSerializationPair = DEFAULT_VALUE_SERIALIZATION_PAIR;
  private CacheKeyPrefix cacheKeyPrefix = name -> "gradation::" + name + "::";
  private boolean allowInFlightCacheCreation;
  private final GradationCacheProperty defaultGradationCacheProperty;


  public GradationCacheManager(GradationCacheProperty defaultGradationCacheProperty, RedisTemplate<String, Object> redisTemplate, boolean allowInFlightCacheCreation) {
    super(redisTemplate);
    this.redisTemplate = redisTemplate;
    this.allowInFlightCacheCreation = allowInFlightCacheCreation;
    this.defaultGradationCacheProperty = defaultGradationCacheProperty;
    cacheManagers.add(this);
  }

  public GradationCacheManager(GradationCacheProperty defaultGradationCacheProperty, RedisTemplate<String, Object> redisTemplate) {
    this(defaultGradationCacheProperty, redisTemplate, true);
  }
  public GradationCacheManager(GradationCacheProperty defaultGradationCacheProperty, RedisTemplate<String, Object> redisTemplate, RemovalListener<Object, Object> removalListener) {
    this(defaultGradationCacheProperty, redisTemplate, true);
    this.removalListener = removalListener;
  }

  @Override
  protected Cache getMissingCache(String name) {
    return allowInFlightCacheCreation ? createGradationCache(name, defaultGradationCacheProperty) : null;
  }

  @Override
  protected Collection<? extends Cache> loadCaches() {
    List<GradationCache> caches = new LinkedList<>();
    for (Map.Entry<String, GradationCacheProperty> entry : initialCacheConfiguration.entrySet()) {
      caches.add(createGradationCache(entry.getKey(), entry.getValue()));
    }
    return caches;
  }

  private GradationCache createGradationCache(String name, @NotNull GradationCacheProperty gradationCacheProperty) {
    CaffeineCacheGdt caffeineCache = null;
    // 如果 enable 创建一级缓存
    if (gradationCacheProperty.isUseFirstCache()) {
      CaffeineCacheProperty caffeineCacheProperty = gradationCacheProperty.getCaffeineCache();
      caffeineCache = createCaffeineCache(name, caffeineCacheProperty, gradationCacheProperty.isAllowNullValue());
    }
    // 创建二级缓存
    RedisCacheProperty redisCacheProperty = gradationCacheProperty.getRedisCache();
    RedisCacheGdt redisCache = createRedisCache(name, redisCacheProperty, redisTemplate, gradationCacheProperty.isAllowNullValue());
    GradationCache gradationCache = new GradationCache(name, gradationCacheProperty.isAllowNullValue(), redisTemplate, caffeineCache, redisCache, gradationCacheProperty.isUseFirstCache());
    gradationCache.setGradationCacheProperty(gradationCacheProperty);
    return gradationCache;
  }

  /**
   * Configuration hook for creating {@link RedisCache} with given name and {@code cacheConfig}.
   *
   *  此处使用自定义锁， 不需要 redis提供的锁操作
   *  RedisCacheWriter.nonLockingRedisCacheWriter
   *
   * @param name        must not be {@literal null}.
   * @param redisCacheProperty must not be {@literal null}.
   * @param redisTemplate must not be {@literal null}.
   * @param allowNullValue must not be {@literal null}.
   */
  public RedisCacheGdt createRedisCache(String name, @NotNull RedisCacheProperty redisCacheProperty, @NotNull RedisTemplate<String, Object> redisTemplate, boolean allowNullValue) {
    final Duration duration = Duration.of(redisCacheProperty.getExpiration(), redisCacheProperty.getTimeUnit().toChronoUnit());
    redisTemplate.setKeySerializer(keySerializationPair);
    redisTemplate.setHashKeySerializer(keySerializationPair);
    redisTemplate.setValueSerializer(valueSerializationPair);
    redisTemplate.setHashValueSerializer(valueSerializationPair);

    final RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        //设置key为String
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializationPair))
        // 设置value 为自动转Json的Object
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializationPair))
        //缓存数据时长
        .entryTtl(duration)
        // 前缀
        .computePrefixWith(cacheKeyPrefix);
    if (!allowNullValue) {
      // 不缓存null
      cacheConfiguration.disableCachingNullValues();
    }

    return new RedisCacheGdt(
        name, RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory()), cacheConfiguration, redisTemplate, redisCacheProperty);
  }
  /**
   * 根据配置获取本地缓存对象
   *
   * @param cacheProperty 一级缓存配置
   * @return {@link com.github.benmanes.caffeine.cache.Cache}
   */
  private CaffeineCacheGdt createCaffeineCache(String name, @NotNull CaffeineCacheProperty cacheProperty, boolean allowNullValue) {
    // 根据配置创建Caffeine builder
    Caffeine<Object, Object> builder = Caffeine.newBuilder()
        .initialCapacity(cacheProperty.getInitialCapacity())
        .maximumSize(cacheProperty.getMaximumSize());
    if (ExpireMode.EXPIRE_AFTER_WRITE.equals(cacheProperty.getExpireMode())) {
      builder.expireAfterWrite(cacheProperty.getExpireTime(), cacheProperty.getTimeUnit());
    } else if (ExpireMode.EXPIRE_AFTER_ACCESS.equals(cacheProperty.getExpireMode())) {
      builder.expireAfterAccess(cacheProperty.getExpireTime(), cacheProperty.getTimeUnit());
    }
    // TODO
    if (Boolean.TRUE.equals(cacheProperty.getWeakKeys())) {
      builder.weakKeys();
    }
    if (Boolean.TRUE.equals(cacheProperty.getWeakValues())) {
      builder.weakValues();
    }
    if (Boolean.TRUE.equals(cacheProperty.getStats())) {
      builder.recordStats();
    }
    if (Objects.nonNull(removalListener)) {
      builder.removalListener(removalListener);
    }
    // 根据Caffeine builder创建 Cache 对象
    com.github.benmanes.caffeine.cache.Cache<Object, Object> cache = builder.build();
    return new CaffeineCacheGdt(name, cache, allowNullValue);
  }

  public void setKeySerializationPair(RedisSerializer<String> keySerializationPair) {
    this.keySerializationPair = keySerializationPair;
  }

  public void setValueSerializationPair(RedisSerializer<Object> valueSerializationPair) {
    this.valueSerializationPair = valueSerializationPair;
  }

  public void setCacheKeyPrefix(CacheKeyPrefix cacheKeyPrefix) {
    this.cacheKeyPrefix = cacheKeyPrefix;
  }

  public CacheKeyPrefix getCacheKeyPrefix() {
    return cacheKeyPrefix;
  }

  public Map<String, GradationCacheProperty> getInitialCacheConfiguration() {
    return initialCacheConfiguration;
  }

  public void setInitialCacheConfiguration(Map<String, GradationCacheProperty> initialCacheConfiguration) {
    this.initialCacheConfiguration = initialCacheConfiguration;
  }

  public void setAllowInFlightCacheCreation(boolean allowInFlightCacheCreation) {
    this.allowInFlightCacheCreation = allowInFlightCacheCreation;
  }

  /**
   * Configurator for creating {@link GradationCacheManager}.
   *
   * @author Christoph Strobl
   * @author Mark Strobl
   * @author Kezhu Wang
   * @since 2.0
   */
  public static class GradationCacheManagerBuilder {
    private final Map<String, GradationCacheProperty> initialCaches = new LinkedHashMap<>();
    private boolean hasInitialCaches = false;
    private final RedisTemplate<String, Object> redisTemplate;
    private RemovalListener<Object, Object> removalListener;
    private boolean allowInFlightCacheCreation = true;
    private GradationCacheProperty defaultCacheConfiguration = new GradationCacheProperty(CaffeineCacheProperty::new, RedisCacheProperty::new);

    private GradationCacheManagerBuilder(RedisTemplate<String, Object> redisTemplate) {
      this.redisTemplate = redisTemplate;
    }

    /**
     * Entry point for builder style {@link GradationCacheManagerBuilder} configuration.
     *
     * @param redisTemplate must not be {@literal null}.
     * @return new {@link GradationCacheManagerBuilder}.
     */
    public static GradationCacheManagerBuilder fromRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
      Assert.notNull(redisTemplate, "RedisTemplate must not be null!");
      return new GradationCacheManagerBuilder(redisTemplate);
    }

    /**
     * Append a {@link Map} of cache name/{@link GradationCacheProperty} pairs to be pre initialized.
     *
     * @param cacheConfigurations must not be {@literal null}.
     * @return this {@link GradationCacheManagerBuilder}.
     */
    public GradationCacheManagerBuilder withInitialCacheConfigurations(
        Map<String, GradationCacheProperty> cacheConfigurations) {
      Assert.notNull(cacheConfigurations, "CacheConfigurations must not be null!");
      cacheConfigurations.forEach((cacheName, configuration) -> Assert.notNull(configuration,
          String.format("RedisCacheConfiguration for cache %s must not be null!", cacheName)));
      this.hasInitialCaches = true;
      this.initialCaches.putAll(cacheConfigurations);

      return this;
    }
    /**
     * Append a {@link RemovalListener} of cache name
     *
     * @param removalListener must not be {@literal null}.
     * @return this {@link GradationCacheManagerBuilder}.
     */
    public GradationCacheManagerBuilder withCaffeineRemovalListener(RemovalListener<Object, Object> removalListener) {
      Assert.notNull(removalListener, "CacheConfigurations must not be null!");
      this.removalListener = removalListener;
      return this;
    }
    /**
     * Disable in-flight {@link org.springframework.cache.Cache} creation for unconfigured caches.
     * <p />
     * {@link GradationCacheManagerBuilder#getMissingCache(String)} returns {@literal null} for any unconfigured
     * {@link org.springframework.cache.Cache} instead of a new {@link RedisCache} instance. This allows eg.
     * {@link org.springframework.cache.support.CompositeCacheManager} to chime in.
     *
     * @return this {@link GradationCacheManagerBuilder}.
     * @since 2.0.4
     */
    public GradationCacheManagerBuilder disableCreateOnMissingCache() {
      this.allowInFlightCacheCreation = false;
      return this;
    }


    /**
     * Create new instance of {@link GradationCacheManager} with configuration options applied.
     *
     * @return new instance of {@link GradationCacheManager}.
     */
    public GradationCacheManager build() {
      GradationCacheManager gm;
      if (Objects.nonNull(removalListener)) {
        gm = new GradationCacheManager(defaultCacheConfiguration, redisTemplate, removalListener);
      } else {
        gm = new GradationCacheManager(defaultCacheConfiguration, redisTemplate);
      }
      if (hasInitialCaches) {
        gm.setInitialCacheConfiguration(initialCaches);
      }
      gm.setAllowInFlightCacheCreation(allowInFlightCacheCreation);
      return gm;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    GradationCacheManager that = (GradationCacheManager) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(allowInFlightCacheCreation, that.allowInFlightCacheCreation)
        .append(redisTemplate, that.redisTemplate)
        .append(initialCacheConfiguration, that.initialCacheConfiguration)
        .append(cacheKeyPrefix, that.cacheKeyPrefix)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(redisTemplate)
        .append(initialCacheConfiguration)
        .append(cacheKeyPrefix)
        .append(allowInFlightCacheCreation)
        .toHashCode();
  }
}

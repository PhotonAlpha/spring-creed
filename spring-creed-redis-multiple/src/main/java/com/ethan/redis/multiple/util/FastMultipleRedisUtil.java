package com.ethan.redis.multiple.util;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

public final class FastMultipleRedisUtil {
  public static final String LETTUCE_CONNECTION_FACTORY = "LettuceConnectionFactory";

  private FastMultipleRedisUtil() {
  }

  public static Supplier<LettuceConnectionFactory> getLettuceConnectionFactory(RedisProperties properties) {
    // RedisStandaloneConfiguration
    RedisStandaloneConfiguration configuration = redisStandaloneConfiguration(properties);

    // GenericObjectPoolConfig
    GenericObjectPoolConfig genericObjectPoolConfig = genericObjectPoolConfig(properties);

    //LettuceConnectionFactory
    return () -> {
      LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
      builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());

      LettuceClientConfiguration clientConfiguration = builder.poolConfig(genericObjectPoolConfig).build();
      return new LettuceConnectionFactory(configuration, clientConfiguration);
    };
  }

  /**
   * 生成 RedisStandaloneConfiguration
   * @param properties
   * @return
   */
  public static RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties properties) {
    // RedisStandaloneConfiguration
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(properties.getHost());
    configuration.setPort(properties.getPort());
    configuration.setDatabase(properties.getDatabase());
    String password = properties.getPassword();
    if (!StringUtils.isEmpty(password)) {
      RedisPassword redisPassword = RedisPassword.of(password);
      configuration.setPassword(redisPassword);
    }
    return configuration;
  }

  /**
   * 生成 Pool Object {@link org.apache.commons.pool2.impl.GenericObjectPool}
   * @param properties
   * @return
   */
  public static GenericObjectPoolConfig genericObjectPoolConfig(RedisProperties properties) {
    GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    RedisProperties.Pool pool = properties.getLettuce().getPool();
    genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
    genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
    genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
    if (pool.getMaxWait() != null) {
      genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
    }
    return genericObjectPoolConfig;
  }
}

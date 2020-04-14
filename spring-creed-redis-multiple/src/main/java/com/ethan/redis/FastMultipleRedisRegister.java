package com.ethan.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Spring Binder 用法,可以方便的转换对象
 * 可用于framework设计
 */
@Slf4j
public class FastMultipleRedisRegister implements EnvironmentAware, ImportBeanDefinitionRegistrar {
  private static Map<String, Object> registerBean = new ConcurrentHashMap<>();
  private Environment env;
  private Binder binder;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    // get all redis config
    Map<String, Map> multipleRedis;
    try {
      multipleRedis = binder.bind("multi.redis", Map.class).get();
    } catch (NoSuchElementException e) {
      log.error("Failed to configure fastDep redis: 'multi.redis' attribute is not specified and no embedded redis could be configured.");
      return;
    }
    boolean onPrimary = true;
    for (String key : multipleRedis.keySet()) {
      Map map = binder.bind("multi.redis." + key, Map.class).get();
      // RedisStandaloneConfiguration
      RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
      configuration.setHostName(String.valueOf(map.get("host")));
      configuration.setPort(Integer.parseInt(String.valueOf(map.get("port"))));
      configuration.setDatabase(Integer.parseInt(String.valueOf(map.get("database"))));
      String password = String.valueOf(map.get("password"));
      if (!StringUtils.isEmpty(password)) {
        RedisPassword redisPassword = RedisPassword.of(password);
        configuration.setPassword(redisPassword);
      }
      // GenericObjectPoolConfig
      GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
      try {
        RedisProperties.Pool pool = binder.bind("multi.redis." + key + ".lettuce.pool", RedisProperties.Pool.class).get();
        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
        if (pool.getMaxWait() != null) {
          genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
      } catch (NoSuchElementException ignore) {
      }
      //LettuceConnectionFactory
      Supplier<LettuceConnectionFactory> lettuceConnectionFactorySupplier = () -> {
        LettuceConnectionFactory factory = (LettuceConnectionFactory) registerBean.get(key + "LettuceConnectionFactory");
        if (factory != null) {
          return factory;
        }
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        try {
          Duration shutdownTimeout = binder.bind("multi.redis." + key + ".shutdown-timeout", Duration.class).get();
          if (shutdownTimeout != null) {
            builder.shutdownTimeout(shutdownTimeout);
          }
        } catch (NoSuchElementException ignore) {
        }
        LettuceClientConfiguration clientConfiguration = builder.poolConfig(genericObjectPoolConfig).build();
        factory = new LettuceConnectionFactory(configuration, clientConfiguration);
        registerBean.put(key + "LettuceConnectionFactory", factory);
        return factory;
      };
      LettuceConnectionFactory lettuceConnectionFactory = lettuceConnectionFactorySupplier.get();
      BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LettuceConnectionFactory.class, lettuceConnectionFactorySupplier);
      AbstractBeanDefinition factoryBean = builder.getRawBeanDefinition();
      factoryBean.setPrimary(onPrimary);
      registry.registerBeanDefinition(key + "LettuceConnectionFactory", factoryBean);
      // StringRedisTemplate
      GenericBeanDefinition stringRedisTemplate = new GenericBeanDefinition();
      stringRedisTemplate.setBeanClass(StringRedisTemplate.class);
      ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
      constructorArgumentValues.addIndexedArgumentValue(0, lettuceConnectionFactory);
      stringRedisTemplate.setConstructorArgumentValues(constructorArgumentValues);
      stringRedisTemplate.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
      registry.registerBeanDefinition(key + "StringRedisTemplate", stringRedisTemplate);
      // RedisTemplate
      GenericBeanDefinition redisTemplate = new GenericBeanDefinition();
      redisTemplate.setBeanClass(RedisTemplate.class);
      redisTemplate.getPropertyValues().add("connectionFactory", lettuceConnectionFactory);
      redisTemplate.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
      registry.registerBeanDefinition(key + "RedisTemplate", redisTemplate);
      log.info("Registration redis ({}) !", key);
      if (onPrimary) {
        onPrimary = false;
      }
    }
    log.info("Registration redis completed !");
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.env = environment;
    binder = Binder.get(this.env);
  }
}

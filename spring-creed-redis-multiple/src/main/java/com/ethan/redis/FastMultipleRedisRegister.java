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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * https://gitee.com/skyarthur1987/spring-boot-demo/tree/redis/
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
    FastMultipleRedisProperties multipleRedis;
    try {
      multipleRedis = binder.bind("multi", FastMultipleRedisProperties.class).get();
    } catch (NoSuchElementException e) {
      log.error("Failed to configure fastDep redis: 'multi.redis' attribute is not specified and no embedded redis could be configured.");
      return;
    }
    boolean onPrimary = true;
    for (Map.Entry<String, RedisProperties> entry : multipleRedis.getRedis().entrySet()) {
      String nickname = entry.getKey();
      RedisProperties properties = entry.getValue();
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
      // GenericObjectPoolConfig
      GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
      RedisProperties.Pool pool = properties.getLettuce().getPool();
      genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
      genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
      genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
      if (pool.getMaxWait() != null) {
        genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
      }

      //LettuceConnectionFactory
      Supplier<LettuceConnectionFactory> lettuceConnectionFactorySupplier = () -> {
        LettuceConnectionFactory factory = (LettuceConnectionFactory) registerBean.get(nickname + "LettuceConnectionFactory");
        if (factory != null) {
          return factory;
        }
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());

        LettuceClientConfiguration clientConfiguration = builder.poolConfig(genericObjectPoolConfig).build();
        factory = new LettuceConnectionFactory(configuration, clientConfiguration);
        registerBean.put(nickname + "LettuceConnectionFactory", factory);
        return factory;
      };
      LettuceConnectionFactory lettuceConnectionFactory = lettuceConnectionFactorySupplier.get();
      BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LettuceConnectionFactory.class, lettuceConnectionFactorySupplier);
      AbstractBeanDefinition factoryBean = builder.getRawBeanDefinition();
      factoryBean.setPrimary(onPrimary);
      registry.registerBeanDefinition(nickname + "LettuceConnectionFactory", factoryBean);
      // StringRedisTemplate
      GenericBeanDefinition stringRedisTemplate = new GenericBeanDefinition();
      stringRedisTemplate.setBeanClass(StringRedisTemplate.class);
      ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
      constructorArgumentValues.addIndexedArgumentValue(0, lettuceConnectionFactory);
      stringRedisTemplate.setConstructorArgumentValues(constructorArgumentValues);
      stringRedisTemplate.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
      registry.registerBeanDefinition(nickname + "StringRedisTemplate", stringRedisTemplate);
      // RedisTemplate
      GenericBeanDefinition redisTemplate = new GenericBeanDefinition();
      redisTemplate.setBeanClass(RedisTemplate.class);
      redisTemplate.getPropertyValues().add("connectionFactory", lettuceConnectionFactory);
      redisTemplate.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
      registry.registerBeanDefinition(nickname + "RedisTemplate", redisTemplate);
      log.info("Registration redis ({}) !", nickname);
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

package com.ethan.redis.multiple;

import com.ethan.redis.multiple.util.FastMultipleRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * https://fastdep.louislivi.com/#/module/fastdep-redis
 * Spring Binder 用法,可以方便的转换对象
 * 可用于framework设计
 */
@Slf4j
public class FastMultipleRedisRegister implements EnvironmentAware, ImportBeanDefinitionRegistrar {
  private static Map<String, Object> registerBean = new ConcurrentHashMap<>();
  private Binder binder;

  public List<Map.Entry<String, RedisProperties>> getEligibleKeys(AnnotationMetadata importingClassMetadata, FastMultipleRedisProperties multipleRedis) {
    Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(FastRedisRegister.class.getName());
    String[] include = (String[]) annotationAttributes.get("include");
    Predicate<Map.Entry<String, RedisProperties>> predicate;
    if (ArrayUtils.isNotEmpty(include)) {
      predicate = str -> Stream.of(include).anyMatch(s -> StringUtils.equalsIgnoreCase(s, str.getKey()));
    } else {
      String[] value = (String[]) annotationAttributes.get("value");
      if (ArrayUtils.isEmpty(value)) {
        String[] exclude = (String[]) annotationAttributes.get("exclude");
        predicate = str -> Stream.of(exclude).noneMatch(s -> StringUtils.equalsIgnoreCase(s, str.getKey()));
      } else {
        predicate = str -> Stream.of(value).noneMatch(s -> StringUtils.equalsIgnoreCase(s, str.getKey()));
      }
    }
    return Optional.ofNullable(multipleRedis)
        .map(FastMultipleRedisProperties::getRedis)
        .map(Map::entrySet).orElse(new HashSet<>())
        .stream().filter(predicate).collect(Collectors.toList());
  }

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

    //AnnotationUtils.getAnnotation(@FastRedisRegister, FastRedisRegister.class)
    // get all redis config
    FastMultipleRedisProperties multipleRedis;
    try {
      multipleRedis = binder.bind("multi", FastMultipleRedisProperties.class).get();
    } catch (NoSuchElementException e) {
      log.error("Failed to configure fastDep redis: 'multi.redis' attribute is not specified and no embedded redis could be configured.");
      return;
    }
    boolean onPrimary = true;
    List<Map.Entry<String, RedisProperties>> entries = getEligibleKeys(importingClassMetadata, multipleRedis);
    for (Map.Entry<String, RedisProperties> entry : entries) {
      String nickname = entry.getKey();
      RedisProperties properties = entry.getValue();
      // RedisStandaloneConfiguration
      RedisStandaloneConfiguration configuration = FastMultipleRedisUtil.redisStandaloneConfiguration(properties);
      // GenericObjectPoolConfig
      GenericObjectPoolConfig genericObjectPoolConfig = FastMultipleRedisUtil.genericObjectPoolConfig(properties);

      //LettuceConnectionFactory
      Supplier<LettuceConnectionFactory> lettuceConnectionFactorySupplier = () -> {
        LettuceConnectionFactory factory = (LettuceConnectionFactory) registerBean.get(nickname + FastMultipleRedisUtil.LETTUCE_CONNECTION_FACTORY);
        if (factory != null) {
          return factory;
        }
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());

        LettuceClientConfiguration clientConfiguration = builder.poolConfig(genericObjectPoolConfig).build();
        factory = new LettuceConnectionFactory(configuration, clientConfiguration);
        registerBean.put(nickname + FastMultipleRedisUtil.LETTUCE_CONNECTION_FACTORY, factory);
        return factory;
      };
      BeanDefinitionBuilder builderProperties = BeanDefinitionBuilder.genericBeanDefinition(RedisProperties.class, () -> properties);
      AbstractBeanDefinition factoryProperties = builderProperties.getRawBeanDefinition();
      factoryProperties.setPrimary(onPrimary);
      registry.registerBeanDefinition(nickname + RedisProperties.class.getSimpleName(), factoryProperties);

      LettuceConnectionFactory lettuceConnectionFactory = lettuceConnectionFactorySupplier.get();
      BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(LettuceConnectionFactory.class, lettuceConnectionFactorySupplier);
      AbstractBeanDefinition factoryBean = builder.getRawBeanDefinition();
      factoryBean.setPrimary(onPrimary);
      registry.registerBeanDefinition(nickname + FastMultipleRedisUtil.LETTUCE_CONNECTION_FACTORY, factoryBean);
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
    binder = Binder.get(environment);
  }
}

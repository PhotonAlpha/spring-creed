package com.ethan.redis;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(FastMultipleRedisProperties.class)
@AutoConfigureBefore(RedisAutoConfiguration.class)
@Import(FastMultipleRedisRegister.class)
public class FastMultipleRedisAutoConfiguration {
}

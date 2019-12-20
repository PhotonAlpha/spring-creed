package com.ethan.cache.redis.annotation;

import com.ethan.cache.config.RedisConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RedisConfig.class)
public @interface EnableDistributedLock {
}

package com.ethan.redis.multiple;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AutoConfigureBefore(RedisAutoConfiguration.class)
@Import(FastMultipleRedisRegister.class)
public @interface FastRedisRegister {
  /**
   * exclude specify connection name
   */
  @AliasFor("exclude")
  String[] value() default {};

  /**
   * include specify connection name only
   */
  String[] include() default {};

  /**
   * exclude specify connection name
   */
  @AliasFor("value")
  String[] exclude() default {};

}

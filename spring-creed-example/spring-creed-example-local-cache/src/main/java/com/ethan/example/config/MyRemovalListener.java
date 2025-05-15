package com.ethan.example.config;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 1/4/25
 */
@Slf4j
public class MyRemovalListener implements RemovalListener<Object, Object> {
    @Override
    public void onRemoval(@Nullable Object key, @Nullable Object value, RemovalCause cause) {
      log.info("onRemoval key:{} value:{} cause:{}", key, value, cause);
    }
}

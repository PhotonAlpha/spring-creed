package com.ethan.gradation.listener;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Slf4j
public class CaffeineRemovalListener implements RemovalListener<Object, Object> {
  @Override
  public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {
    log.info("CacheRemovalListener:{} cause:{}", key, cause);
  }
}

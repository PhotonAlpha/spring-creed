package com.ethan.cache.listener;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

public class CaffeineRemovalListener implements RemovalListener<Object, Object> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(CaffeineRemovalListener.class);

	@Override
  public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {
    log.info("CacheRemovalListener:{} cause:{}", key, cause);
  }
}

package com.ethan.cache.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayeringBean {
  private String cacheName = "default-cache";
  private CaffeineCacheBean caffeine = new CaffeineCacheBean();
  private RedisCacheBean redis = new RedisCacheBean();
}

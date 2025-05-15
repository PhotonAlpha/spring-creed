package com.ethan.cache.model;

import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaffeineCacheBean {
  private Integer initialCapacity = 10;
  private Long maximumSize = 50L;
  private Long expireAfterWriteMins = 30L;
  private RemovalListener removalListener;
}

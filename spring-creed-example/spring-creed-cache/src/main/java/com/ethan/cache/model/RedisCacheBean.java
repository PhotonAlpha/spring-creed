package com.ethan.cache.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisCacheBean {
  private Boolean usePrefix = true;
  private Long preloadTime = 5L;
  // unit seconds
  private Long expirationTime = 30L;
  private Boolean forceRefresh = false;
  private Boolean enablePrimaryCache = true;

}

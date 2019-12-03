package com.ethan.cache.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "caffeine")
public class CacheBeans {
  private List<CacheConfig> configs = Arrays.asList(new CacheConfig());

  @Getter
  @Setter
  public static class CacheConfig {
    private String cacheName = "default-cache";
    private Integer initialCapacity = 10;
    private Long maximumSize = 50L;
    private Long expireAfterWriteMins = 30L;
  }
}

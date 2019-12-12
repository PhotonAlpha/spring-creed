package com.ethan.cache.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisCacheBean {
  private List<RedisCacheConfiguration> configs = Arrays.asList(new RedisCacheConfiguration());

  @Getter
  @Setter
  public static class RedisCacheConfiguration {
    private String cacheName = "default-redis";
    private Long preloadTime = 50L;
    // unit seconds
    private Long expirationTime = 30L;
    private Boolean forceRefresh = false;
  }

  public Map<String, RedisCacheConfiguration> getCacheSettings() {
    return configs.stream().collect(Collectors.toMap(RedisCacheConfiguration::getCacheName, v -> v, (v1, v2) -> v1));
  }

}

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
@ConfigurationProperties(prefix = "caffeine")
public class CaffeineCacheBean {
  private List<CaffeineCacheConfiguration> configs = Arrays.asList(new CaffeineCacheConfiguration());

  @Getter
  @Setter
  public static class CaffeineCacheConfiguration {
    private String cacheName = "default-caffeine";
    private Integer initialCapacity = 10;
    private Long maximumSize = 50L;
    private Long expireAfterWriteMins = 30L;
  }

  public Map<String, CaffeineCacheConfiguration> getCacheSettings() {
    return configs.stream().collect(Collectors.toMap(CaffeineCacheConfiguration::getCacheName, v -> v, (v1, v2) -> v1));
  }
}

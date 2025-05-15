package com.ethan.example.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;


@Getter
@Setter
@Configuration
@ConfigurationProperties("caffeine")
public class CachePropertiesBean {

  public static final String DEFAULT_CACHE = "default-cache";

  private List<CacheConfig> configs = List.of(new CacheConfig(DEFAULT_CACHE, 10, 50L, 10L));

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CacheConfig {
    private String cacheName;
    private Integer initialCapacity;
    private Long maximumSize;
    private Long expireAfterWriteMins;
  }
}

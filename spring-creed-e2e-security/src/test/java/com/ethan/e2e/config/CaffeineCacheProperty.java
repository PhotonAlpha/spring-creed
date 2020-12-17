package com.ethan.e2e.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 一级缓存配置项
 */
@Getter
@Setter
@Component
@ConfigurationProperties("caffeine")
@ToString
public class CaffeineCacheProperty {
  private List<CaffeineConfig> configs = Arrays.asList(new CaffeineConfig("symmetric-cache", 50, 500L, 90L));

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  static class CaffeineConfig {
    private String cacheName;
    private Integer initialCapacity = 50;
    private Long maximumSize = 500L;
    private Long expireAfterWriteMins = 90L;
  }
}

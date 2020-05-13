package com.ethan.gradation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("spring")
@Data
public class GradationConfigurationMapper {
  // with default config
  private Map<String, GradationCacheProperty> gradation;

}

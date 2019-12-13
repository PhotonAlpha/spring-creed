package com.ethan.cache.config;

import com.ethan.cache.model.LayeringBean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * also can reference {@link org.springframework.boot.autoconfigure.cache.CacheProperties#CacheProperties()}
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "layering")
public class CacheProperties {
  private List<LayeringBean> config = Arrays.asList(new LayeringBean());
}

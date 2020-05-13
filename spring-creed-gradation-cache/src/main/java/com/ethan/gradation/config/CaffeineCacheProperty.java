package com.ethan.gradation.config;

import com.ethan.gradation.constant.ExpireMode;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 一级缓存配置项
 */
@Data
@ToString
public class CaffeineCacheProperty implements Serializable {
  private static final long serialVersionUID = -6760605715913035210L;
  /**
   * 缓存初始Size
   */
  private Integer initialCapacity = 10;

  /**
   * 缓存最大Size
   */
  private Long maximumSize = 50L;

  /**
   * 缓存有效时间
   */
  private Long expireTime = 600L;

  /**
   * 缓存时间单位
   */
  private TimeUnit timeUnit = TimeUnit.SECONDS;

  /**
   * 缓存失效模式{@link ExpireMode}
   */
  private ExpireMode expireMode = ExpireMode.EXPIRE_AFTER_WRITE;

  /**
   * 是否开启统计
   */
  private Boolean stats = false;
  private Boolean weakValues = true;
  private Boolean weakKeys = false;
}

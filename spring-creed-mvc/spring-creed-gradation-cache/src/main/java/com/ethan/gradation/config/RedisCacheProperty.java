package com.ethan.gradation.config;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 一级缓存配置项
 */
@Data
public class RedisCacheProperty implements Serializable {
  private static final long serialVersionUID = -1339251457977664555L;
  /**
   * 缓存有效时间
   */
  private Long expiration = 1200L;

  /**
   * 缓存主动在失效前强制刷新缓存的时间
   * 建议 expiration * 0.2
   */
  private Long preloadTime = 24L;

  /**
   * 时间单位 {@link TimeUnit}
   */
  private TimeUnit timeUnit = TimeUnit.SECONDS;

  /**
   * 是否强制刷新（走数据库），默认是false
   */
  private Boolean forceRefresh = false;

  /**
   * 是否使用缓存名称作为 redis key 前缀
   */
  private Boolean usePrefix = true;

  /**
   * 非空值和null值之间的时间倍率，默认是1。allowNullValue=true才有效
   * <p>
   * 如配置缓存的有效时间是200秒，倍率这设置成10，
   * 那么当缓存value为null时，缓存的有效时间将是20秒，非空时为200秒
   * </p>
   */
  Integer magnification = 1;

  private Boolean stats = false;

}

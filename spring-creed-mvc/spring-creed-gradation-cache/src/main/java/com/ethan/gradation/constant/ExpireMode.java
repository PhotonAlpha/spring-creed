package com.ethan.gradation.constant;

/**
 * 缓存失效模式
 */
public enum ExpireMode {
  /**
   * 每写入一次重新计算一次缓存的有效时间
   */
  EXPIRE_AFTER_WRITE,

  /**
   * 每访问一次重新计算一次缓存的有效时间
   */
  EXPIRE_AFTER_ACCESS
}

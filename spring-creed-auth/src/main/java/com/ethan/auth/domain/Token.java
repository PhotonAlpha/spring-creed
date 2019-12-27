package com.ethan.auth.domain;

import lombok.Data;

import java.util.List;

@Data
public class Token {
  /**
   * 过期时间
   */
  private String expiration;
  /**
   * 是否过期
   */
  private boolean expired;
  /**
   * 过期时限
   */
  private int expiresIn;
  /**
   * refreshToken对象
   */
  private RefreshTokenBean refreshToken;

  /**
   * token类型
   */
  private String tokenType;

  /**
   * access_token值
   */
  private String value;

  /**
   * 使用范围
   */
  private List<String> scope;
}

package com.ethan.auth.domain;

import lombok.Data;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;

import java.util.Date;

@Data
public class ExpiringRefreshToken implements ExpiringOAuth2RefreshToken {
  /**
   * 过期时间
   */
  private Date expiration;
  /**
   * token值
   */
  private String value;

}

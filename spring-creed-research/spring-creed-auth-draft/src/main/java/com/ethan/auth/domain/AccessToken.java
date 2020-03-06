package com.ethan.auth.domain;

import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
public class AccessToken implements OAuth2AccessToken {
  private String value;

  private Date expiration;

  private String tokenType = BEARER_TYPE.toLowerCase();

  private Set<String> scope;

  private Map<String, Object> additionalInformation = Collections.emptyMap();
  /**
   * refreshToken对象
   */
  private ExpiringRefreshToken refreshToken;

  @Override
  public ExpiringRefreshToken getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(ExpiringRefreshToken refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override
  public boolean isExpired() {
    return expiration != null && expiration.before(new Date());
  }

  @Override
  public int getExpiresIn() {
    return expiration != null ? Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
        .intValue() : 0;
  }
}

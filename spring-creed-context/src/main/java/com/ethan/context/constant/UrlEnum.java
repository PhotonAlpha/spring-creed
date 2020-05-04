package com.ethan.context.constant;

import lombok.Getter;

@Getter
public enum UrlEnum {
  //oauth2登录
  LOGIN_URL("/oauth/token"),
  AUTHORIZATION("clientapp", "112233", "authorization_code", "http://localhost:8080/auth/grant")
  ;

  private String url;
  private String clientId;
  private String clientSecret;
  private String grantType;
  private String redirectUri;

  UrlEnum(String url) {
    this.url = url;
  }

  UrlEnum(String clientId, String clientSecret, String grantType, String redirectUri) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.grantType = grantType;
    this.redirectUri = redirectUri;
  }

  public String getUrl() {
    return url;
  }
}

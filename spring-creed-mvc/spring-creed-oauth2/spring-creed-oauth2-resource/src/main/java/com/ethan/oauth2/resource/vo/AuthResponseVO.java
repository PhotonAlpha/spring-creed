package com.ethan.oauth2.resource.vo;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.io.Serializable;

/**
 * 扩展 OAuth2Exception， 返回自定义异常信息
 * @param <T>
 */
public class AuthResponseVO<T> extends ResponseVO<T> implements Serializable {
  private static final long serialVersionUID = 266208288778904286L;

  public static ResponseVO error(OAuth2Exception oEx){
    return new ResponseVO(HttpStatus.BAD_REQUEST.value(), oEx.getOAuth2ErrorCode(), oEx.getMessage());
  }
}

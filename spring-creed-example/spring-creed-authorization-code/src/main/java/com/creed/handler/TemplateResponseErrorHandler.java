package com.creed.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 自定义restTemplate异常
 * 为了适配 {@link org.springframework.security.oauth2.common.exceptions.OAuth2Exception} 的处理方式，可以借鉴此处处理方式
 * 返回必要的 OAuth2Exception 用于封装
 */
public class TemplateResponseErrorHandler extends DefaultResponseErrorHandler {
  private final ObjectMapper mapper = new ObjectMapper();
  @Override
  protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
    byte[] body = getResponseBody(response);
    Charset charset = getCharset(response);
    String strBody = new String(body, charset);
    if (HttpStatus.BAD_REQUEST == statusCode && isJSONValid(strBody)) {
      throw mapper.readValue(strBody, OAuth2Exception.class);
    } else {
      super.handleError(response, statusCode);
    }
  }


  private boolean isJSONValid(String content) {
    try {
      mapper.readTree(content);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}

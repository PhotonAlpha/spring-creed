package com.ethan.auth.handler;

import com.ethan.auth.constants.ResponseEnum;
import com.ethan.auth.vo.ResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Slf4j
public class UnAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler, Serializable {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    Throwable cause = authException.getCause();
    if (cause instanceof InvalidTokenException) {
      log.error("InvalidTokenException : {}",cause.getMessage());
      //Token无效
      response.getWriter().write(MAPPER.writeValueAsString(ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID)));
    } else {
      log.error("AuthenticationException : NoAuthentication");
      //资源未授权
      response.getWriter().write(MAPPER.writeValueAsString(ResponseVO.error(ResponseEnum.UNAUTHORIZED)));
    }
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    //访问资源的用户权限不足
    log.error("AccessDeniedException : {}",accessDeniedException.getMessage());
    response.getWriter().write(MAPPER.writeValueAsString(ResponseVO.error(ResponseEnum.INSUFFICIENT_PERMISSIONS)));
  }
}

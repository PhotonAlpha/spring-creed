package com.ethan.core.config;

import com.ethan.core.constant.Constant;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

public class UnAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    Object status = request.getAttribute(Constant.REQUEST_STATUS);
    String traceId = response.getHeader(Constant.TRACE_ID);
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, traceId + "Unauthorized" + status);
  }
}

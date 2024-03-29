package com.ethan.oauth2.resource.handler;

import com.ethan.oauth2.resource.constent.ResponseEnum;
import com.ethan.oauth2.resource.vo.AuthResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

/**
 * @description 自定义未授权 token无效 权限不足返回信息处理类
 * @date 2019/3/4 15:49
 */
@Slf4j
public class UnAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler, Serializable {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Throwable cause = authException.getCause();

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (cause instanceof InvalidTokenException) {
            log.error("InvalidTokenException : {}",cause.getMessage());
            //Token无效
            response.getWriter().write(MAPPER.writeValueAsString(AuthResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID)));
        } else {
            log.error("AuthenticationException : NoAuthentication");
            //资源未授权
            response.getWriter().write(MAPPER.writeValueAsString(AuthResponseVO.error(ResponseEnum.UNAUTHORIZED)));
        }

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        /*response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Cache-Control","no-cache");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.addHeader("Access-Control-Max-Age", "1800");*/
        //访问资源的用户权限不足
        log.error("AccessDeniedException : {}",accessDeniedException.getMessage());
        response.getWriter().write(MAPPER.writeValueAsString(AuthResponseVO.error(ResponseEnum.INSUFFICIENT_PERMISSIONS)));
    }
}

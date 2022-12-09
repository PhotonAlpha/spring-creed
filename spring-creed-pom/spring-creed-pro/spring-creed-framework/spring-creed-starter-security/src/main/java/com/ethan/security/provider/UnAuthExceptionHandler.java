package com.ethan.security.provider;

import com.ethan.common.common.R;
import com.ethan.common.exception.enums.ResponseCodeEnum;
import com.ethan.common.utils.servlet.ServletUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

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

        //AuthenticationException
        if (cause instanceof AccessDeniedException) {
            log.error("AccessDeniedException : {}",cause.getMessage());
            //Token无效
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletUtils.writeJSON(response, R.error(ResponseCodeEnum.ACCESS_TOKEN_INVALID));
            // response.getWriter().write(MAPPER.writeValueAsString(R.error(ResponseCodeEnum.ACCESS_TOKEN_INVALID)));
        } else {
            // response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realmName + "\"");
            //         response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            log.error("AuthenticationException : NoAuthentication");
            //资源未授权
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletUtils.writeJSON(response, R.error(ResponseCodeEnum.UNAUTHORIZED));
        }

    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        //访问资源的用户权限不足
        log.error("AccessDeniedException : {}",accessDeniedException.getMessage());
        if (accessDeniedException instanceof InvalidCsrfTokenException) {
            ServletUtils.writeJSON(response, R.error(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage()));
        } else if (accessDeniedException instanceof MissingCsrfTokenException) {
            ServletUtils.writeJSON(response, R.error(HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage()));
        } else {
            ServletUtils.writeJSON(response, R.error(ResponseCodeEnum.INSUFFICIENT_PERMISSIONS));
        }

    }
}

package com.ethan.security.provider;

import com.ethan.common.common.R;
import com.ethan.common.exception.enums.ResponseCodeEnum;
import com.ethan.common.utils.servlet.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description 自定义未授权 token无效 权限不足返回信息处理类
 * @date 2019/3/4 15:49
 */
@Slf4j
public class UnAuthExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler, Serializable {
    private String realmName;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Throwable cause = authException.getCause();
        log.error("AuthenticationEntryPoint : {}",request.getRequestURI(), authException);
        //AuthenticationException
        if (cause instanceof AccessDeniedException) {
            log.error("AccessDeniedException : {}", cause.getMessage());
            //Token无效
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletUtils.writeJSON(response, R.error(ResponseCodeEnum.ACCESS_TOKEN_INVALID));
            // response.getWriter().write(MAPPER.writeValueAsString(R.error(ResponseCodeEnum.ACCESS_TOKEN_INVALID)));
        } else if (cause instanceof InvalidBearerTokenException || cause instanceof BadOpaqueTokenException) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            Map<String, String> parameters = new LinkedHashMap<>();
            if (this.realmName != null) {
                parameters.put("realm", this.realmName);
            }
            if (authException instanceof OAuth2AuthenticationException) {
                OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
                parameters.put("error", error.getErrorCode());
                if (StringUtils.hasText(error.getDescription())) {
                    parameters.put("error_description", error.getDescription());
                }
                if (StringUtils.hasText(error.getUri())) {
                    parameters.put("error_uri", error.getUri());
                }
                if (error instanceof BearerTokenError) {
                    BearerTokenError bearerTokenError = (BearerTokenError) error;
                    if (StringUtils.hasText(bearerTokenError.getScope())) {
                        parameters.put("scope", bearerTokenError.getScope());
                    }
                    status = ((BearerTokenError) error).getHttpStatus();
                }
            }
            String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
            response.setStatus(status.value());
            ServletUtils.writeJSON(response, R.error(ResponseCodeEnum.ACCESS_TOKEN_INVALID, wwwAuthenticate));
        } else {
            // response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realmName + "\"");
            //         response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
            log.error("AuthenticationException : NoAuthentication");
            //资源未授权
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletUtils.writeJSON(response, R.error(ResponseCodeEnum.UNAUTHORIZED));
        }

    }

    private static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
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


    /**
     * Set the default realm name to use in the bearer token error response
     * @param realmName
     */
    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
}

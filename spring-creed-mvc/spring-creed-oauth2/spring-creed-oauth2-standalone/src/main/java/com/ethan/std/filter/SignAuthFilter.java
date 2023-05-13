package com.ethan.std.filter;

import com.ethan.std.constant.Constant;
import com.ethan.std.exception.InvalidRequestTimeOrNonceException;
import com.ethan.std.exception.InvalidSignatureException;
import com.ethan.std.utils.DateUtil;
import com.ethan.std.utils.SignUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/16/2022 3:47 PM
 */
public class SignAuthFilter extends OncePerRequestFilter {
    private static final String SHOULD_NOT_FILTER = "SHOULD_NOT_FILTER" + CsrfFilter.class.getName();

    private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Boolean.TRUE.equals(request.getAttribute(SHOULD_NOT_FILTER));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String timestamp = request.getHeader(Constant.SIGN_TIME);
        String nonce = request.getHeader(Constant.SIGN_NONCE);
        String sign = request.getHeader(Constant.SIGN);
        /**
         ContentCachingRequestWrapper reqWrapper = new ContentCachingRequestWrapper((HttpServletRequest) req);
         ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper((HttpServletResponse) res);
         try {
            chain.doFilter(reqWrapper, resWrapper);
            resWrapper.copyBodyToResponse();  // Necessary (see answer by StasKolodyuk above)
         } catch (IOException | ServletException e) {
            log.error("Error extracting body", e);
         }

         if (req instanceof ContentCachingRequestWrapper) {
             ContentCachingRequestWrapper reqWrapper = (ContentCachingRequestWrapper) req;
             String payload = new String (reqWrapper.getContentAsByteArray(), "utf-8");
             log.debug("Request [ {} ] has payload [ {} ]", reqWrapper.getRequestURI(), payload);
         }
         */

        // 时间限制配置
        long nonce_str_timeout_seconds = 60L;
        if (StringUtils.isBlank(timestamp) || DateUtil.expired(timestamp, nonce_str_timeout_seconds)) {
            this.logger.debug("request timestamp expired " + UrlUtils.buildFullRequestUrl(request));
            AccessDeniedException exception = new InvalidRequestTimeOrNonceException(timestamp);
            this.accessDeniedHandler.handle(request, response, exception);
            return;
        }
        //判断该用户的nonce参数是否已经存在（防止短时间内的重放攻击）
        if (StringUtils.isBlank(nonce)) {
            this.logger.debug("invalid nonce " + UrlUtils.buildFullRequestUrl(request));
            AccessDeniedException exception = new InvalidRequestTimeOrNonceException(nonce);
            this.accessDeniedHandler.handle(request, response, exception);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        String requestPayload = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.displayName());
        //对请求头参数进行签名
        if (StringUtils.isBlank(sign) || !StringUtils.equals(sign, SignUtils.signature(timestamp, nonce, requestPayload, requestWrapper))) {
            this.logger.debug("invalid signature " + UrlUtils.buildFullRequestUrl(request));
            AccessDeniedException exception = new InvalidSignatureException(sign);
            this.accessDeniedHandler.handle(request, response, exception);
            return;
        }

        // 将本次用户请求的nonce参数存到内存中，设置xx秒后自动删除
        storeNonce(nonce);

        filterChain.doFilter(requestWrapper, response);
    }

    private void storeNonce(String nonce) {
        //TODO store in cache or redis
    }

    public static void skipRequest(HttpServletRequest request) {
        request.setAttribute(SHOULD_NOT_FILTER, Boolean.TRUE);
    }


    /**
     * Specifies a {@link AccessDeniedHandler} that should be used when CSRF protection
     * fails.
     *
     * <p>
     * The default is to use AccessDeniedHandlerImpl with no arguments.
     * </p>
     * @param accessDeniedHandler the {@link AccessDeniedHandler} to use
     */
    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        Assert.notNull(accessDeniedHandler, "accessDeniedHandler cannot be null");
        this.accessDeniedHandler = accessDeniedHandler;
    }
}

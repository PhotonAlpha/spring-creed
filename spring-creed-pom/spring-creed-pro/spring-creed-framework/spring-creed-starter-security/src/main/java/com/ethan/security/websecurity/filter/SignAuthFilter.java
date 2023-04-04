package com.ethan.security.websecurity.filter;


import com.ethan.common.constant.CommonConstants;
import com.ethan.common.exception.util.SignUtils;
import com.ethan.common.utils.date.DateUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/16/2022 3:47 PM
 *
 * <b>.addFilterAfter(applicationContext.getBean(SignAuthFilter.class), TokenAuthenticationFilter.class);</b><br/>
 *
 *
 * 后端sign+timestamp+nonce防止请求被篡改的实现思路如下：
 *
 * 1.在每次请求中，客户端需要发送一个nonce（随机数），一个timestamp（时间戳）和一个签名sign。
 * 2.服务器接收到请求后，先验证timestamp是否过期，如果过期则认为请求非法，直接拒绝。
 * 3.验证nonce是否已经被使用过，如果已经被使用过，则认为请求非法，直接拒绝。
 * 4.服务器使用同样的算法，对请求中的参数进行签名，然后将服务器端的签名与请求中的签名进行比较，如果一致则说明请求合法，否则说明请求被篡改，直接拒绝。
 * 5.如果请求合法，服务器将保存nonce，并执行请求。
 *
 * 具体实现可以参考以下步骤：
 *
 * 1.定义一个常量或配置文件，用于存储服务器端的密钥。
 * 2.在客户端发送请求时，生成一个随机数nonce和一个当前时间的时间戳timestamp，并将其添加到请求参数中。
 * 3.将请求参数和密钥使用同样的算法进行签名，得到一个sign值，并将其添加到请求参数中。
 * 4.服务器接收到请求后，首先验证timestamp是否过期，如果过期则认为请求非法，直接拒绝。
 * 5.验证nonce是否已经被使用过，如果已经被使用过，则认为请求非法，直接拒绝。
 * 6.服务器从请求参数中获取nonce、timestamp和sign值，并使用同样的算法进行签名，得到一个新的sign值。
 * 7.将服务器端的sign值与请求中的sign值进行比较，如果一致则说明请求合法，否则说明请求被篡改，直接拒绝。
 * 8.如果请求合法，服务器将保存nonce，并执行请求。
 *
 */
public class SignAuthFilter extends OncePerRequestFilter {
    private static final String SHOULD_NOT_FILTER = "SHOULD_NOT_FILTER" + CsrfFilter.class.getName();
    public static final CreedBearerTokenResolver RESOLVER = new CreedBearerTokenResolver();
    private static final Long TIMEOUT_SECONDS = 30L;
    // private static final Long TIMEOUT_SECONDS = 3600L;

    private AccessDeniedHandler accessDeniedHandler;

    // private final CaseReplayLogDao caseReplayLogDao;
    private final WebProperties webProperties;

/*     public SignAuthFilter(AccessDeniedHandler accessDeniedHandler, CaseReplayLogDao caseReplayLogDao, WebProperties webProperties) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.caseReplayLogDao = caseReplayLogDao;
        this.webProperties = webProperties;
    } */

    public SignAuthFilter(AccessDeniedHandler accessDeniedHandler, WebProperties webProperties) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.webProperties = webProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Boolean.TRUE.equals(request.getAttribute(SHOULD_NOT_FILTER));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = RESOLVER.resolve(request);
        if (StringUtils.isNotEmpty(token)) {
            String timestamp = RESOLVER.resolve(request, CommonConstants.SIGN_TIME);
            String nonce = RESOLVER.resolve(request, CommonConstants.SIGN_NONCE);
            String sign = RESOLVER.resolve(request, CommonConstants.SIGN);
            synchronized (this) {
                try {
                    if (StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(sign)) {
                        this.logger.debug("both timestamp & nonce & sign can not be empty" + UrlUtils.buildFullRequestUrl(request));
                        AccessDeniedException exception = new AuthorizationServiceException("both timestamp & nonce & sign can not be empty " + UrlUtils.buildFullRequestUrl(request));
                        this.accessDeniedHandler.handle(request, response, exception);
                        return;
                    }
                    // check is expired
                    if (DateUtils.expired(timestamp, TIMEOUT_SECONDS)) {
                        this.logger.debug("request timestamp expired " + UrlUtils.buildFullRequestUrl(request));
                        AccessDeniedException exception = new AuthorizationServiceException("request timestamp expired " + UrlUtils.buildFullRequestUrl(request));
                        this.accessDeniedHandler.handle(request, response, exception);
                        return;
                    }

                    //check nonce existing
                    // Optional<CaseReplayLog> replayLogOptional = caseReplayLogDao.findByNonce(token, nonce, timestamp);
                    // if (replayLogOptional.isPresent()) {
                    //     this.logger.debug("invalid nonce " + UrlUtils.buildFullRequestUrl(request));
                    //     AccessDeniedException exception = new AuthorizationServiceException("invalid nonce " + UrlUtils.buildFullRequestUrl(request));
                    //     this.accessDeniedHandler.handle(request, response, exception);
                    //     return;
                    // }

                    String generatedSignature = SignUtils.generateSignature(token, nonce, timestamp, getRequestBody(request));
                    if (StringUtils.isBlank(sign) || !StringUtils.equals(sign, generatedSignature)) {
                        this.logger.debug("invalid signature " + UrlUtils.buildFullRequestUrl(request));
                        AccessDeniedException exception1 = new AuthorizationServiceException("invalid signature " + UrlUtils.buildFullRequestUrl(request));
                        this.accessDeniedHandler.handle(request, response, exception1);
                        return;
                    }

                    // storeNonce, will be deleted after 1 hour
                    storeNonce(token, nonce, timestamp, request);

                } catch (Exception e) {
                    this.logger.debug("internal error " + ExceptionUtils.getRootCause(e));
                    AccessDeniedException exception = new AuthorizationServiceException("internal error " + ExceptionUtils.getRootCause(e));
                    this.accessDeniedHandler.handle(request, response, exception);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void storeNonce(String traceId, String nonce, String timestamp, HttpServletRequest request) {
        //store in redis

    }

    public static void skipRequest(HttpServletRequest request) {
        request.setAttribute(SHOULD_NOT_FILTER, Boolean.TRUE);
    }

    private String getRequestBody(HttpServletRequest cachedRequest) {
        if (StringUtils.startsWith(cachedRequest.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return IOUtils.toString(((CachedBodyHttpServletRequest) cachedRequest).getBody(), StandardCharsets.UTF_8.displayName());
        } else if (StringUtils.startsWith(cachedRequest.getContentType(), MediaType.MULTIPART_FORM_DATA_VALUE)){
            // https://github.com/spring-projects/spring-framework/issues/29562
            StandardServletMultipartResolver commonsMultipartResolver = new StandardServletMultipartResolver();
            MultipartHttpServletRequest resolveMultipart = commonsMultipartResolver.resolveMultipart(cachedRequest);
            if (resolveMultipart.getParameterMap().containsKey("xxx")) {
                return resolveMultipart.getParameter("xxx");
            }
        }
        return "";
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

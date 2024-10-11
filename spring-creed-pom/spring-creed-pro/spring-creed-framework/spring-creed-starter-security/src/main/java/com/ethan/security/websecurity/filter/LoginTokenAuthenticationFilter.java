/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.websecurity.filter;

import com.ethan.common.exception.ServiceException;
import com.ethan.security.utils.SecurityFrameworkUtils;
import com.ethan.security.websecurity.config.CreedSecurityProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class LoginTokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private OAuth2AuthorizationService authorizationService;
    @Resource
    private CreedSecurityProperties securityProperties;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        String token = defaultBearerTokenResolver.resolve(request);
        boolean matched = Optional.ofNullable(securityProperties).map(CreedSecurityProperties::getPermitAllUrls)
                .orElse(Collections.emptyList())
                .stream().anyMatch(str -> new AntPathRequestMatcher(str).matches(request));
        if (StringUtils.isBlank(token) || matched) {
            log.warn("Did not process request since did not find bearer token or by pass already");
            chain.doFilter(request, response);
            return;
        }
        OAuth2Authorization authorizedClient = null;
        if (Boolean.TRUE.equals(securityProperties.getMockEnable())) {
            BearerTokenAuthentication authentication = (BearerTokenAuthentication) SecurityFrameworkUtils.getAuthentication();
            assert authentication != null;
            authorizedClient = OAuth2Authorization.withRegisteredClient(
                            RegisteredClient.withId(UUID.randomUUID().toString())
                                    .clientId("mock-client")
                                    .clientSecret("{noop}mock")
                                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                                    .build())
                    .id("-1")
                    .principalName("1")
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .authorizedScopes(Collections.singleton("message.read")).build();
        } else {
            authorizedClient = buildLoginUserByToken(token);
        }

        SecurityFrameworkUtils.setLoginUser(authorizedClient, request);
        chain.doFilter(request, response);
    }

    private OAuth2Authorization buildLoginUserByToken(String token, Integer userType) {
        try {
            OAuth2Authorization accessToken = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
            if (Objects.isNull(accessToken)) {
                return null;
            }
            // 用户类型不匹配，无权限

            // 构建登录用户
            return accessToken;
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }
    private OAuth2Authorization buildLoginUserByToken(String token) {
        try {
            OAuth2Authorization accessToken = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
            if (Objects.isNull(accessToken)) {
                return null;
            }
            // 用户类型不匹配，无权限

            // 构建登录用户
            return accessToken;
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }
}

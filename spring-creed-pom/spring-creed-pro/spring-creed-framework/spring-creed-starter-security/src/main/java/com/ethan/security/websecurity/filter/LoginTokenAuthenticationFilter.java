/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.websecurity.filter;

import com.ethan.common.exception.ServiceException;
import com.ethan.common.utils.WebFrameworkUtils;
import com.ethan.security.oauth2.entity.CreedOAuth2AuthorizedClient;
import com.ethan.security.oauth2.repository.CreedOAuth2AuthorizedClientRepository;
import com.ethan.security.utils.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class LoginTokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private CreedOAuth2AuthorizedClientRepository clientRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        String token = defaultBearerTokenResolver.resolve(request);
        CreedOAuth2AuthorizedClient authorizedClient = buildLoginUserByToken(token, WebFrameworkUtils.getLoginUserType(request));

        SecurityFrameworkUtils.setLoginUser(authorizedClient, request);
        chain.doFilter(request, response);
    }

    private CreedOAuth2AuthorizedClient buildLoginUserByToken(String token, Integer userType) {
        try {
            Optional<CreedOAuth2AuthorizedClient> accessToken = clientRepository.findByAccessTokenValue(token);
            if (accessToken.isEmpty()) {
                return null;
            }
            // 用户类型不匹配，无权限

            // 构建登录用户
            return accessToken.get();
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }
}

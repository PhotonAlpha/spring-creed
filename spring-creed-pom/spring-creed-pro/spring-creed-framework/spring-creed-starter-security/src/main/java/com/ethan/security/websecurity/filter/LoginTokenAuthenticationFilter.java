/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.websecurity.filter;

import com.ethan.common.exception.ServiceException;
import com.ethan.common.utils.WebFrameworkUtils;
import com.ethan.security.oauth2.entity.CreedOAuth2Authorization;
import com.ethan.security.oauth2.entity.client.CreedOAuth2AuthorizedClient;
import com.ethan.security.oauth2.repository.CreedOAuth2AuthorizationRepository;
import com.ethan.security.oauth2.repository.client.CreedOAuth2AuthorizedClientRepository;
import com.ethan.security.utils.SecurityFrameworkUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class LoginTokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    //    private CreedOAuth2AuthorizedClientRepository clientRepository;
    private CreedOAuth2AuthorizationRepository clientRepository;
    @Resource
    private CreedOAuth2AuthorizedClientRepository authorizedClientRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        String token = defaultBearerTokenResolver.resolve(request);
        if (StringUtils.isBlank(token)) {
            log.debug("Did not process request since did not find bearer token");
            chain.doFilter(request, response);
            return;
        }
        CreedOAuth2AuthorizedClient authorizedClient = buildLoginUserByToken(token);

        SecurityFrameworkUtils.setLoginUser(authorizedClient, request);
        chain.doFilter(request, response);
    }

    private CreedOAuth2Authorization buildLoginUserByToken(String token, Integer userType) {
        try {
            Optional<CreedOAuth2Authorization> accessToken = clientRepository.findByAccessTokenValue(token);
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
    private CreedOAuth2AuthorizedClient buildLoginUserByToken(String token) {
        try {
            Optional<CreedOAuth2AuthorizedClient> authorizedClientOptional = authorizedClientRepository.findByAccessTokenValue(token);
            if (authorizedClientOptional.isEmpty()) {
                return null;
            }
            // 用户类型不匹配，无权限

            // 构建登录用户
            return authorizedClientOptional.get();
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }
}

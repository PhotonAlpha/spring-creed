package com.ethan.security.websecurity.filter;

import com.ethan.common.utils.WebFrameworkUtils;
import com.ethan.security.websecurity.config.CreedSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;
import java.util.Optional;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 7/19/24
 *
 * 根据 {@link CreedSecurityProperties#getPermitAllUrls()} 如果headers中有一些无效token，直接跳过验证
 */
@Slf4j
@AllArgsConstructor
public class DefaultTokenIntrospectorProvider implements OpaqueTokenIntrospector {

    private CreedSecurityProperties securityProperties;

    OpaqueTokenIntrospector alternativeOpaqueTokenIntrospector;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        HttpServletRequest request = WebFrameworkUtils.getRequest();
        boolean matched = Optional.ofNullable(securityProperties).map(CreedSecurityProperties::getPermitAllUrls)
                .orElse(Collections.emptyList())
                .stream().anyMatch(str -> new AntPathRequestMatcher(str).matches(request));
        if (matched) {
            log.warn("default bypass bearer token enabled. Please don't enable in PROD.");
            // bypass if the endpoint enabled
            return new OAuth2IntrospectionAuthenticatedPrincipal(Collections.singletonMap("GUEST", "NA"), Collections.singleton(new SimpleGrantedAuthority("AnonymousAccess")));
        }
        return alternativeOpaqueTokenIntrospector.introspect(token);
    }
}

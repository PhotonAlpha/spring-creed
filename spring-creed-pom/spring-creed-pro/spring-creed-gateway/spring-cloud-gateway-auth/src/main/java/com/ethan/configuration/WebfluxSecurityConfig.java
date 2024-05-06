package com.ethan.configuration;

import com.ethan.utils.JwtTokenUtil;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@EnableWebFluxSecurity
public class WebfluxSecurityConfig {
    @Resource
    private DefaultAuthorizationManager defaultAuthorizationManager;

    @Resource
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Resource
    private DefaultAuthenticationSuccessHandler defaultAuthenticationSuccessHandler;

    @Resource
    private DefaultAuthenticationFailureHandler defaultAuthenticationFailureHandler;

    @Resource
    private TokenAuthenticationManager tokenAuthenticationManager;

    @Resource
    private DefaultSecurityContextRepository defaultSecurityContextRepository;

    @Resource
    private DefaultAuthenticationEntryPoint defaultAuthenticationEntryPoint;

    @Resource
    private DefaultAccessDeniedHandler defaultAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authenticationManager(reactiveAuthenticationManager())
                .securityContextRepository()
        ;
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password("{noop}password")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    /**
     * 注册用户信息验证管理器，可按需求添加多个按顺序执行
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        LinkedList<ReactiveAuthenticationManager> managers = new LinkedList<>();
        managers.add(authentication -> {
            // 其他登陆方式 (比如手机号验证码登陆) 可在此设置不得抛出异常或者 Mono.error
            return Mono.empty();
        });
        // 必须放最后不然会优先使用用户名密码校验但是用户名密码不对时此 AuthenticationManager 会调用 Mono.error 造成后面的 AuthenticationManager 不生效
        managers.add(new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService()));
        managers.add(new JwtReactiveAuthenticationManager());
        return new DelegatingReactiveAuthenticationManager(managers);
    }

    /* private static class TokenAuthenticationManager implements ReactiveAuthenticationManager {
        @Override
        public Mono<Authentication> authenticate(Authentication authentication) {
            return Mono.just(authentication)
                    .map(auth -> JwtTokenUtil.parseJwtRsa256(auth.getPrincipal().toString()))
                    .map(claims -> {
                        Collection<? extends GrantedAuthority> roles = (Collection<? extends GrantedAuthority>)                     claims.get("roles");
                        return new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                roles
                        );
                    });
        }
    } */
    @Bean
    public JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource) {
        return OAuth2ResourceServerConfigurer.jwtDecoder(jwkSource);
    }
    private static class DefaultSecurityContextRepository implements ServerSecurityContextRepository {
        public final static String TOKEN_HEADER = "Authorization";

        public final static String BEARER = "Bearer ";

        @Override
        public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
            return Mono.empty();
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange exchange) {
            ServerHttpRequest request = exchange.getRequest();
            List<String> headers = request.getHeaders().get(TOKEN_HEADER);
            if (!CollectionUtils.isEmpty(headers)) {
                String authorization = headers.get(0);
                if (StringUtils.isNotEmpty(authorization)) {
                    String token = authorization.substring(BEARER.length());
                    if (StringUtils.isNotEmpty(token)) {
                        return tokenAuthenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(token, null)
                        ).map(SecurityContextImpl::new);
                    }
                }
            }
            return Mono.empty();
        }
    }
}

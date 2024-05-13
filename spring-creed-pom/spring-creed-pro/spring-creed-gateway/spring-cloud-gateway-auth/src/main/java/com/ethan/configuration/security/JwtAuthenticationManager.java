package com.ethan.configuration.security;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

// @Component
//TODO
// JWT 访问有待参考：https://www.cnblogs.com/weixia-blog/p/16688273.html
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    // @Resource
    // private TokenStore jwtTokenStore;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return null;
    }
}

package com.ethan.configuration.security;

import com.ethan.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

// @Component
@Slf4j
public class TokenAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> JwtTokenUtil.parseJwtRsa256(auth.getPrincipal().toString()))
                .map(claims -> {
                    Collection<? extends GrantedAuthority> roles = (Collection<? extends GrantedAuthority>) claims.get("roles");
                    return new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),
                            null,
                            roles
                    );
                })
                // .onErrorComplete(err -> {
                //     return new BadCredentialsException("");
                // })
                ;
    }
}

package com.ethan.agent.factory.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 20/11/24
 */
@Slf4j
public class CreedBuddyRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("@.@[CreedBuddyRequestFilter calling]@.@");
        try {
            //add mock SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(genericUsernamePasswordAuthenticationToken());
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private Authentication genericUsernamePasswordAuthenticationToken() {
        return UsernamePasswordAuthenticationToken.authenticated("Anonymous",
                null, List.of(new SimpleGrantedAuthority("ADMIN")));
    }
}

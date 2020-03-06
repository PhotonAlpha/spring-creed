package com.creed.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String username = "user2";
    String password = "user2";
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails details = userDetailsService.loadUserByUsername(username);
      //List<GrantedAuthority> grantedAuth = AuthorityUtils.createAuthorityList(details.getAuthorities());
      List<GrantedAuthority> grantedAuth = new ArrayList<>(details.getAuthorities());
      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, grantedAuth);
      // 填充webDetails [remoteAddress, sessionId]
      authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authRequest);
    }
    filterChain.doFilter(request, response);
  }
}

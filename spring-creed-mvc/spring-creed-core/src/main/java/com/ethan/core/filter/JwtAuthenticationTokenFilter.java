package com.ethan.core.filter;

import com.ethan.core.constant.Constant;
import com.ethan.core.util.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
  private final JwtTokenUtils tokenUtils;
  @Value("${jwt.header:Authorization}")
  private String tokenHeader;

  public JwtAuthenticationTokenFilter(JwtTokenUtils tokenUtils) {
    this.tokenUtils = tokenUtils;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    final String requestHeader = request.getHeader(tokenHeader);
    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
      String authToken = requestHeader.substring(7);
      String username = tokenUtils.getUsernameFromToken(authToken);
      if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (!tokenUtils.isTokenExpired(authToken)) {
          log.info("authenticated user {}, setting security context", username);
          SecurityContextHolder.getContext().setAuthentication(genericUsernamePasswordAuthenticationToken(request));
        } else {
          request.setAttribute(Constant.REQUEST_STATUS, "token invalid");
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private UsernamePasswordAuthenticationToken genericUsernamePasswordAuthenticationToken(HttpServletRequest request) {
    UserDetails userDetails = new User("creed", "", Arrays.asList(new SimpleGrantedAuthority("ADMIN")));
    // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return authentication;
  }
}

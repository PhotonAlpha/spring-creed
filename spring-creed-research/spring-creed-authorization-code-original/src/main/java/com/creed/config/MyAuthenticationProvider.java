package com.creed.config;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

public class MyAuthenticationProvider implements AuthenticationProvider {
  private final UserDetailsService userDetailsService;

  public MyAuthenticationProvider(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  /**
   * can reference {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider}
   * @param authentication
   * @return
   * @throws AuthenticationException
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    UserDetails account = userDetailsService.loadUserByUsername(username);
    if (account == null) {
      throw new AuthenticationCredentialsNotFoundException("Account is not found.");
    }
    List<GrantedAuthority> grantedAuth = new ArrayList<>(account.getAuthorities());
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password, grantedAuth);
    //authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return authRequest;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}

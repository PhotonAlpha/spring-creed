package com.creed;

import com.creed.model.Account;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public class CustomAuthenticationProvider implements AuthenticationProvider {
  private final UserDetailsService userDetailsService;

  public CustomAuthenticationProvider(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    Account account = (Account)userDetailsService.loadUserByUsername(username);
    if (account == null) {
      throw new AuthenticationCredentialsNotFoundException("Account is not found.");
    }
    List<GrantedAuthority> grantedAuth = AuthorityUtils.createAuthorityList(account.getRoleString());
    return new UsernamePasswordAuthenticationToken(username, password, grantedAuth);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}

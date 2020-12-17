package com.ethan.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UserDetailsService userService;

  @Override
  public Authentication authenticate(Authentication authentication) {
    final String name = authentication.getName();
    final String password = authentication.getCredentials().toString();
     log.info("Authenticating for user: {} with password: {}", name, password);

    final UserDetails userDetails = userService.loadUserByUsername(name);
    if (userDetails == null || !matchPassword(password, userDetails.getPassword())) {
      throw new BadCredentialsException("Invalid username and password combination");
    }
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return false;
  }

  public CustomAuthenticationProvider setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
    return this;
  }

  private boolean matchPassword(String rawPassword, String encodedPassword) {
    return this.passwordEncoder.matches(rawPassword, encodedPassword);
  }
}

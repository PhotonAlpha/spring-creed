package com.ethan.controller;

import com.ethan.core.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api")
public class AuthenticationController {
  @Value("${jwt.header}")
  private String tokenHeader;
  private final UserDetailsService userDetailsService;
  private final JwtTokenUtils jwtTokenUtil;

  public AuthenticationController(UserDetailsService userDetailsService, JwtTokenUtils jwtTokenUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
  }


  @GetMapping(value = "/user")
  public ResponseEntity<UserDetails> getUsers(HttpServletRequest request) {
    String token = request.getHeader(tokenHeader).substring(7);
    String username = jwtTokenUtil.getUsernameFromToken(token);
    UserDetails user = userDetailsService.loadUserByUsername(username);
    return ResponseEntity.ok(user);
  }
}

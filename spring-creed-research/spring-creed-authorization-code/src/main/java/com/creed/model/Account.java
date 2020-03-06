package com.creed.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
@Builder
//@Accessors(chain = true)
public class Account implements UserDetails {
  private Integer id;
  private String username;
  private String email;
  private String password;
  private String roleString;
  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean credentialsNonExpired;
  private boolean enabled;
  private List<? extends GrantedAuthority> authorities;
}

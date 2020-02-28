package auth.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

@Data
public class CustomUserDetail implements UserDetails, Serializable {
  private static final long serialVersionUID = 1861865549254074548L;

  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 账户生效
   */
  private boolean accountNonExpired = true;
  /**
   * 账户锁定
   */
  private boolean accountNonLocked = true;
  /**
   * 凭证生效
   */
  private boolean credentialsNonExpired = true;
  /**
   * 激活状态
   */
  private boolean enabled = true;
  /**
   * 权限列表
   */
  private Collection<GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}

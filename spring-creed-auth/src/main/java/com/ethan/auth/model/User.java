package com.ethan.auth.model;

import com.ethan.auth.domain.Base;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "um_t_user")
public class User extends Base implements Serializable {
  private static final long serialVersionUID = -6545036420825695333L;
  /**
   * 用户账号
   */
  @Column(name = "account")
  private String account;

  /**
   * 用户名
   */
  @Column(name = "name")
  private String name;

  /**
   * 用户密码
   */
  @Column(name = "password")
  private String password;

  /**
   * 用户 --角色 多对一
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinTable(name = "um_t_role_user", joinColumns = {@JoinColumn(name = "userId")}, inverseJoinColumns = {@JoinColumn(name = "roleId")})
  private Role role;
}

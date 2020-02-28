package com.ethan.auth.vo;

import lombok.Data;

@Data
public class UserVO {
  /**
   * 用户账号
   */
  private String account;

  /**
   * 用户名
   */
  private String name;

  /**
   * 用户密码
   */
  private String password;

  /**
   * 用户角色
   */
  private RoleVO role;
}

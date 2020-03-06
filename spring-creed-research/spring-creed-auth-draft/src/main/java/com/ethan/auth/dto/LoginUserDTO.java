package com.ethan.auth.dto;

import lombok.Data;

@Data
public class LoginUserDTO {
  /**
   * 用户名
   */
  private String account;

  /**
   * 用户密码
   */
  private String password;
}

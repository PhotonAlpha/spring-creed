package com.ethan.auth.dto;

import lombok.Data;

@Data
public class ModifyUserDTO {
  /**
   * 原密码
   */
  String oldPassword;

  /**
   * 新密码
   */
  String newPassword;
}

package com.ethan.app.dto;

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

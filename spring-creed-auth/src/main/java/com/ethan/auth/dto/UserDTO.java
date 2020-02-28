package com.ethan.auth.dto;

import com.ethan.auth.model.Base;
import lombok.Data;

@Data
public class UserDTO extends Base {
  /**
   * 用户名
   */
  private String account;
  /**
   * 用户姓名
   */
  private String name;
  /**
   * 用户密码
   */
  private String password;
  /**
   * 用户角色id
   */
  private Long roleId;
}

package com.ethan.auth.vo;

import com.ethan.auth.model.Base;
import lombok.Data;

@Data
public class RoleVO {
  /**
   * 角色名(中文)
   */
  private String name;

  /**
   * 角色名
   */
  private String role;
}

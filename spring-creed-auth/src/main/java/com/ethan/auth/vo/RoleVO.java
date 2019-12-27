package com.ethan.auth.vo;

import com.ethan.auth.domain.Base;
import lombok.Data;

@Data
public class RoleVO extends Base {
  /**
   * 角色名(中文)
   */
  private String name;

  /**
   * 角色名
   */
  private String role;
}

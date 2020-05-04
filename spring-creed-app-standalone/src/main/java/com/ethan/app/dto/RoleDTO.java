package com.ethan.app.dto;

import com.ethan.entity.AuthorityEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO {
  private AuthorityEnum roleName;
}

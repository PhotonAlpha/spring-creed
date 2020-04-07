package com.ethan.vo;

import com.ethan.entity.GroupEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupVO {
  private Long groupParentId = 0L;

  private GroupEnum groupName;
}

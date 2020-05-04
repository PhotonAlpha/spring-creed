package com.ethan.vo;

import com.ethan.entity.BaseDO;
import com.ethan.entity.BlogDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelVO extends BaseDO {
  private Long labelId;

  private Long labelName;

  private String labelAlias;

  private String labelDescription;

  private List<BlogDO> blogList;
}

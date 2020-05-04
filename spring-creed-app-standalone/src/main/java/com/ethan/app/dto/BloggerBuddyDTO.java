package com.ethan.app.dto;

import com.ethan.entity.BaseDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 用户好友列表
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BloggerBuddyDTO extends BaseDO {
  private Long bbId;

  /**
   * 博主ID
   */
  private Long bbBloggerId;

  /**
   * 博主好友ID
   */
  private Long bbBuddyId;

  private String bbBuddyRemark;

  private String bbBuddyStatus;

}

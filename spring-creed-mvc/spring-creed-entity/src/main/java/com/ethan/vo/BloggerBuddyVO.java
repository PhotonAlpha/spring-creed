package com.ethan.vo;

import com.ethan.entity.BaseDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户好友列表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BloggerBuddyVO extends BaseDO {
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

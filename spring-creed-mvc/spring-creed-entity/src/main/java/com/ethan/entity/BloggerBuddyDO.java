package com.ethan.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户好友列表
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "ethan_blogger_buddy")
public class BloggerBuddyDO extends BaseDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bb_id", length = 20)
  private Long bbId;

  /**
   * 博主ID
   */
  @Column(name = "bb_b_id", length = 20)
  @NotNull(message = "bbBloggerId can not empty")
  private Long bbBloggerId;

  /**
   * 博主好友ID
   */
  @NotNull(message = "bbBloggerId can not empty")
  @Column(name = "bb_buddy_id", length = 20)
  private Long bbBuddyId;

  @Column(name = "bb_buddy_remark", length = 20)
  private String bbBuddyRemark;

  @Column(name = "bb_buddy_status", length = 20)
  private String bbBuddyStatus;

}

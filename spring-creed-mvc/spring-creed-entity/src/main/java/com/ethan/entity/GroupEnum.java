package com.ethan.entity;

public enum GroupEnum {
  //用户组可以包括 超级管理员 风纪委员会 版主 子版主
  /**
   * 超级管理员
   */
  SUPERVISOR,
  /**
   * 风纪委员会
   */
  DISCIPLINE_MEMBERS,
  /**
   * 版主
   */
  MODERATOR,
  /**
   * 子版主
   */
  SUB_MODERATOR;
}

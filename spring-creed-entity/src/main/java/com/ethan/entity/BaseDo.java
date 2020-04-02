package com.ethan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseDo implements Serializable {
  private static final long serialVersionUID = 2780090478054977516L;

  /**
   * 添加时间
   */
  @Column(name = "created_time")
  protected Date createdTime;

  @Column(name = "updated_time")
  protected Date updatedTime;


  /**
   * 是否激活
   */
  @Column(name = "active")
  protected Boolean active;
}

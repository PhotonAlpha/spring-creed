package com.ethan.app.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class Base implements Serializable {

  private static final long serialVersionUID = 7160606988568555740L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  /**
   * 添加时间
   */
  @Column(name = "created_time")
  protected Date createdTime;


  /**
   * 描述
   */
  protected String description;
}

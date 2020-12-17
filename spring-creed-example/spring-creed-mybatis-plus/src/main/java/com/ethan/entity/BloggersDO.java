package com.ethan.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ethan_bloggers")
public class BloggersDO {
  @Id
  @Column(name = "b_id", length = 20)
  private Long id;

  @Column(name = "b_name", length = 20)
  private String name;
}

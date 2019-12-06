package com.ethan.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dictionary")
@Data
@Accessors(chain = true)
public class AppDictionary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "key", unique = true, length = 50)
  private String key;
  @Column(name = "value", length = 255)
  private String value;
  @Column(name = "group_name", length = 50)
  private String groupName;
}

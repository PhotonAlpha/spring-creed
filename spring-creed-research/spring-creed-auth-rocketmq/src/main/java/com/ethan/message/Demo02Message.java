package com.ethan.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Demo02Message {
  public static final String TOPIC = "DEMO_02";

  /**
   * 编号
   */
  private Integer id;
}

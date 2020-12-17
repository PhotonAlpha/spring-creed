package com.ethan.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Demo01Message {
  public static final String TOPIC = "DEMO_01";

  /**
   * 编号
   */
  private Integer id;
}

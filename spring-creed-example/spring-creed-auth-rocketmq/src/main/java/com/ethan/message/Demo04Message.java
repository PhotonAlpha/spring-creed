package com.ethan.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Demo04Message {
  public static final String TOPIC = "DEMO_04";

  /**
   * 编号
   */
  private Integer id;
}

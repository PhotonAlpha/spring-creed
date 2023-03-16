/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.message;

import lombok.Data;

@Data
public class Demo05Message {
    public static final String TOPIC = "DEMO_05";
    /**
     * 编号
     */
    private Integer id;
}

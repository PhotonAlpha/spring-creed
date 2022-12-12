/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.apm.state.entity;

import com.ethan.apm.state.constant.OrderStatus;
import lombok.Data;
import lombok.ToString;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/11/2022 4:47 PM
 */
@Data
@ToString
public class Order {
    private int id;
    private OrderStatus status;


}

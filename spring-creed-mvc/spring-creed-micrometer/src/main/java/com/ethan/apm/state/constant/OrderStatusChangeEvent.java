/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.apm.state.constant;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/11/2022 4:49 PM
 *
 * 订单状态改变事件
 */
public enum OrderStatusChangeEvent {
    //支付，发货，确认收货
    PAYED, DELIVERY, RECEIVED;
}

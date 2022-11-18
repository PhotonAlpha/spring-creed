package com.ethan.apm.state.constant;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/11/2022 4:48 PM
 * 订单状态
 */
public enum OrderStatus {
    //待支付，待发货，待收货，订单结束
    WAIT_PAYMENT, WAIT_DELIVER, WAIT_RECEIVE, FINISH;
}

package com.ethan.creedmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.common.utils.PageUtils;
import com.ethan.creedmall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:42:02
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

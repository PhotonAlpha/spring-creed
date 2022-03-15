package com.ethan.creedmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.common.utils.PageUtils;
import com.ethan.creedmall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:42:02
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


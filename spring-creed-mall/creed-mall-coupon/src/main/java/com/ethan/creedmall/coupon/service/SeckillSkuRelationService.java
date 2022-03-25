package com.ethan.creedmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:48:19
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


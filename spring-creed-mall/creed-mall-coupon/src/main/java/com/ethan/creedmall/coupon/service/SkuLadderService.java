package com.ethan.creedmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.coupon.entity.SkuLadderEntity;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:48:19
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


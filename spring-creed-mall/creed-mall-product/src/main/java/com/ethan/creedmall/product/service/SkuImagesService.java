package com.ethan.creedmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:46:13
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


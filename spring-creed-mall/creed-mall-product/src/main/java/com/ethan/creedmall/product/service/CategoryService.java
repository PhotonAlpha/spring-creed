package com.ethan.creedmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:46:13
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    boolean removeMenuByIds(List<Long> ids);
}


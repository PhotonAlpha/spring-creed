package com.ethan.creedmall.product.dao;

import com.ethan.creedmall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:46:13
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}

package com.ethan.creedmall.order.dao;

import com.ethan.creedmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:42:02
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}

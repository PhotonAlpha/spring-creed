package com.ethan.creedmall.member.dao;

import com.ethan.creedmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:50:10
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}

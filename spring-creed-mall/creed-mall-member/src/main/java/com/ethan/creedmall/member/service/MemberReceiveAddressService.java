package com.ethan.creedmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ethan.creedmall.common.utils.PageUtils;
import com.ethan.creedmall.member.entity.MemberReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author ethan
 * @email ethan.caoq@foxmail.com
 * @date 2022-03-15 19:50:11
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


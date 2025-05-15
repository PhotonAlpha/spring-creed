package com.ethan.system.service.member;

/**
 * Member Service 接口
 *
 * 
 */
public interface MemberService {

    /**
     * 获得会员用户的手机号码
     *
     * @param id 会员用户编号
     * @return 手机号码
     */
    String getMemberUserMobile(Long id);

}

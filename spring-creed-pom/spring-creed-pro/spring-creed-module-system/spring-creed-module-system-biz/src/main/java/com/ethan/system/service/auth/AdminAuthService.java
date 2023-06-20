/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.service.auth;

import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.ethan.system.controller.admin.auth.vo.AuthSmsLoginReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthSmsSendReqVO;
import com.ethan.system.controller.admin.auth.vo.AuthSocialLoginReqVO;
import jakarta.validation.Valid;

import java.util.concurrent.CompletableFuture;

public interface AdminAuthService {
    /**
     * 验证账号 + 密码。如果通过，则返回用户
     *
     * @param username 账号
     * @param password 密码
     * @return 用户
     */
    CreedUser authenticate(String username, String password);

    /**
     * 账号登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO login(@Valid AuthLoginReqVO reqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     * @param logType 登出类型
     */
    void logout(String token, Integer logType);

    /**
     * 短信验证码发送
     *
     * @param reqVO 发送请求
     */
    void sendSmsCode(AuthSmsSendReqVO reqVO);

    /**
     * 短信登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO) ;

    /**
     * 社交快捷登录，使用 code 授权码
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AuthLoginRespVO socialLogin(@Valid AuthSocialLoginReqVO reqVO);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthLoginRespVO refreshToken(String refreshToken);


    CompletableFuture<String> async1();

    CompletableFuture<String> async2();
}

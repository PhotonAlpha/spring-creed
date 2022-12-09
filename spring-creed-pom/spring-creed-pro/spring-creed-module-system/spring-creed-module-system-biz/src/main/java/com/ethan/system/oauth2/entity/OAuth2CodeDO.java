package com.ethan.system.oauth2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * OAuth2 授权码 DO
 *
 * @author 芋道源码
 */
// @Table(name = "system_oauth2_code_seq")
// @Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class OAuth2CodeDO extends BaseDO {

    /**
     * 编号，数据库递增
     */
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 授权码
     */
    private String code;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * 客户端编号
     *
     * 关联 {@link OAuth2ClientDO#getClientId()}
     */
    private String clientId;
    /**
     * 授权范围
     */
    private String scopes;
    /**
     * 重定向地址
     */
    private String redirectUri;
    /**
     * 状态
     */
    private String state;
    /**
     * 过期时间
     */
    private Date expiresTime;

}

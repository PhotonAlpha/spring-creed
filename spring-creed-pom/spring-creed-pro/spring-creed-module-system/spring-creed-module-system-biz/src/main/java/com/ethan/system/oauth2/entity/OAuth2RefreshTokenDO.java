package com.ethan.system.oauth2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * OAuth2 刷新令牌
 *
 * @author 芋道源码
 */
// @Table(name = "system_oauth2_access_token_seq")
// @Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Deprecated
public class OAuth2RefreshTokenDO extends BaseDO {

    /**
     * 编号，数据库字典
     */
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 刷新令牌
     */
    private String refreshToken;
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
     * 关联 {@link OAuth2ClientDO#getId()}
     */
    private String clientId;
    /**
     * 授权范围
     */
    private String scopes;
    /**
     * 过期时间
     */
    private Date expiresTime;

}

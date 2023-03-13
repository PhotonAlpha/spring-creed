package com.ethan.system.dal.entity.oauth2;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.converter.ListJacksonConverter;
import com.ethan.system.dal.entity.tenant.TenantBaseDO;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * OAuth2 访问令牌 DO
 *
 * 如下字段，暂时未使用，暂时不支持：
 * user_name、authentication（用户信息）
 *
 * 
 */
@Table(name = "system_oauth2_access_token")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Deprecated(forRemoval = true)
public class OAuth2AccessTokenDO extends TenantBaseDO {

    /**
     * 编号，数据库递增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 用户编号
     */
    private String userId;
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
    // @TableField(typeHandler = JacksonTypeHandler.class)
    @Convert(converter = ListJacksonConverter.class)
    private List<String> scopes;
    /**
     * 过期时间
     */
    private Date expiresTime;

}

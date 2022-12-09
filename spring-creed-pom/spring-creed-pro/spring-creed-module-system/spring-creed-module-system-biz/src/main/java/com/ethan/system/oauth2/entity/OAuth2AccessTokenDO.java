package com.ethan.system.oauth2.entity;

import com.ethan.common.constant.UserTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.ap.internal.model.GeneratedType;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OAuth2 访问令牌 DO
 *
 * 如下字段，暂时未使用，暂时不支持：
 * user_name、authentication（用户信息）
 *
 * @author 芋道源码
 */
// @Table(name = "system_oauth2_access_token")
// @Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class OAuth2AccessTokenDO extends TenantBaseDO {

    /**
     * 编号，数据库递增
     */
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    // @Column(name = "user_type")
    private Integer userType;

    public UserTypeEnum getUserType() {
        return UserTypeEnum.parse(this.userType);
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType.getValue();
    }

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

    public Set<String> getScopes() {
        return Optional.ofNullable(this.scopes)
                .map(s -> s.replaceAll("\\s+", ","))
                .map(org.springframework.util.StringUtils::commaDelimitedListToStringArray)
                .map(Stream::of).orElse(Stream.empty())
                .collect(Collectors.toSet());
    }

    /**
     * 过期时间
     */
    private Date expiresTime;

}

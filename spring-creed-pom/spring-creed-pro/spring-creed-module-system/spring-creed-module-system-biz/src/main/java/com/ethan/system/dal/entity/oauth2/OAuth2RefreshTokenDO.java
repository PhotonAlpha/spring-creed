package com.ethan.system.dal.entity.oauth2;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.converter.ListJacksonConverter;
import com.ethan.common.pojo.BaseDO;
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
 * OAuth2 刷新令牌
 *
 * 
 */
@Table(name = "system_oauth2_refresh_token")
// 由于 Oracle 的 SEQ 的名字长度有限制，所以就先用 system_oauth2_access_token_seq 吧，反正也没啥问题
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Deprecated
public class OAuth2RefreshTokenDO extends BaseDO {

    /**
     * 编号，数据库字典
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    // @TableField(typeHandler = JacksonTypeHandler.class)
    @Convert(converter = ListJacksonConverter.class)
    private List<String> scopes;
    /**
     * 过期时间
     */
    private Date expiresTime;

}

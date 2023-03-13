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
 * OAuth2 授权码 DO
 *
 * 
 */
@Table(name = "system_oauth2_code")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Deprecated
public class OAuth2CodeDO extends BaseDO {

    /**
     * 编号，数据库递增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    // @TableField(typeHandler = JacksonTypeHandler.class)
    @Convert(converter = ListJacksonConverter.class)
    private List<String> scopes;
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

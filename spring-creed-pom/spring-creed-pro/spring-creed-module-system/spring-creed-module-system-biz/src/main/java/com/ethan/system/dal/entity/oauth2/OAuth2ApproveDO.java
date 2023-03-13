package com.ethan.system.dal.entity.oauth2;

import com.ethan.common.constant.UserTypeEnum;
import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * OAuth2 批准 DO
 *
 * 用户在 sso.vue 界面时，记录接受的 scope 列表
 *
 * 
 */
@Table(name = "system_oauth2_approve")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Deprecated
public class OAuth2ApproveDO extends BaseDO {

    /**
     * 编号，数据库自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private String scope;
    /**
     * 是否接受
     *
     * true - 接受
     * false - 拒绝
     */
    private Boolean approved;
    /**
     * 过期时间
     */
    private Date expiresTime;

}

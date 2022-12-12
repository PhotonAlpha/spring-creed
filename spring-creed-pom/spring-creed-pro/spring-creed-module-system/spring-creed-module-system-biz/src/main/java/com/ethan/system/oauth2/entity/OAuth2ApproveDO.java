package com.ethan.system.oauth2.entity;

import com.ethan.common.constant.UserTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * OAuth2 批准 DO
 *
 * 用户在 sso.vue 界面时，记录接受的 scope 列表
 *
 * @author 芋道源码
 */
// @Table(name = "system_oauth2_approve")
// @Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class OAuth2ApproveDO extends BaseDO {

    /**
     * 编号，数据库自增
     */
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
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

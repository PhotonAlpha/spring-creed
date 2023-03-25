/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.entity.tenant;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.BaseDO;
import com.ethan.system.dal.entity.user.AdminUserDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_tenant")
@Data
@EqualsAndHashCode
@ToString(exclude = "consumerAuthorities")
// @AttributeOverride(name = "enabled", column = @Column(name = "deleted"))
public class TenantDO extends BaseDO {
    /**
     * 租户编号，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 租户名，唯一
     */
    private String name;
    /**
     * 联系人的用户编号
     *
     * 关联 {@link AdminUserDO#getId()}
     */
    @Column(name = "contact_user_id")
    private Long contactUserId;
    /**
     * 联系人
     */
    @Column(name = "contact_name")
    private String contactName;
    /**
     * 联系手机
     */
    @Column(name = "contact_mobile")
    private String contactMobile;
    /**
     * 租户状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 绑定域名
     *
     * TODO 芋艿：目前是预留字段，未来会支持根据域名，自动查询到对应的租户。等等
     */
    private String domain;
    /**
     * 租户套餐编号
     *
     * 关联 {@link TenantPackageDO#getId()}
     * 特殊逻辑：系统内置租户，不使用套餐，暂时使用 {@link #PACKAGE_ID_SYSTEM} 标识
     */
    @Column(name = "package_id")
    private Long packageId;
    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    /**
     * 账号数量
     */
    @Column(name = "account_count")
    private Integer accountCount;
}

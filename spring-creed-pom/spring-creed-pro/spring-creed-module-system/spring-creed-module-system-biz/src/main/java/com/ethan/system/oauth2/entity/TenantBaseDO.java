package com.ethan.system.oauth2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拓展多租户的 BaseDO 基类
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public abstract class TenantBaseDO extends BaseDO {

    /**
     * 多租户编号
     */
    private Long tenantId;

}

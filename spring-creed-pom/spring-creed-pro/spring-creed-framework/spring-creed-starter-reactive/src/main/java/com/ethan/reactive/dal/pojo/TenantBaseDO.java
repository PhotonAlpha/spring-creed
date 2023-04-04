package com.ethan.reactive.dal.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 拓展多租户的 BaseDO 基类
 *
 * 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public abstract class TenantBaseDO extends BaseDO {

    /**
     * 多租户编号
     */
    private Long tenantId;

}

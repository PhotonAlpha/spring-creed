package com.ethan.common.pojo;

import com.ethan.common.constant.CommonStatusEnum;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * basic 增强 enabled 启用/关闭属性
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@Accessors(chain = true)
// @EntityListeners(BaseXAuditingEntityListener.class)
public abstract class BaseVersioningXDO extends BaseVersioningDO {
    /**
     * 是否启用
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    @Convert(converter = CommonStatusEnum.Converter.class)
    protected CommonStatusEnum enabled = CommonStatusEnum.ENABLE;

}

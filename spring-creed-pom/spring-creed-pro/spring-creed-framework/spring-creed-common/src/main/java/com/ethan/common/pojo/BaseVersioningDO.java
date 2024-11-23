package com.ethan.common.pojo;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * basic Object
 * including version control
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@Accessors(chain = true)
public abstract class BaseVersioningDO extends BaseDO {
    @Version
    protected int version;
}

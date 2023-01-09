package com.ethan.entity;

import com.ethan.common.constant.CommonStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;

/**
 * basic Object
 */
@Data
@MappedSuperclass
public abstract class BaseDO implements Serializable {
    /**
     * 创建时间
     */
    // @CreatedDate TODO 需要使用spring security 框架
    protected Instant createTime;
    /**
     * 最后更新时间
     */
    // @LastModifiedDate
    protected Instant updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    // @CreatedBy
    protected String creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    // @LastModifiedBy
    protected String updater;

    /**
     * 是否删除
     *
     * 枚举 {@link CommonStatusEnum}
     */
    @Convert(converter = CommonStatusEnum.Converter.class)
    private CommonStatusEnum enabled= CommonStatusEnum.ENABLE;

    @Version
    private int version;

}

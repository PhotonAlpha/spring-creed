package com.ethan.common.pojo;

import com.ethan.common.constant.CommonStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * basic Object
 */
@Data
@MappedSuperclass
@Accessors(chain = true)
public abstract class BaseDO implements Serializable {
    /**
     * 创建时间
     */
    // @CreatedDate TODO 需要使用spring security 框架
    @Column(name = "create_time")
    @CreationTimestamp
    protected ZonedDateTime createTime;
    /**
     * 最后更新时间
     */
    // @LastModifiedDate
    @Column(name = "update_time")
    @UpdateTimestamp
    protected ZonedDateTime updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    // @CreatedBy
    protected String creator = "default";
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     * <p>
     * 使用 String 类型的原因是，未来可能会存在非数值的情况，留好拓展性。
     */
    // @LastModifiedBy
    protected String updater = "default";

    /**
     * 是否删除
     *
     * 枚举 {@link CommonStatusEnum}
     */
    // @Convert(converter = CommonStatusEnum.Converter.class)
    // protected CommonStatusEnum enabled= CommonStatusEnum.ENABLE;
    @Convert(converter = CommonStatusEnum.Converter.class)
    protected CommonStatusEnum deleted= CommonStatusEnum.ENABLE;

/*     @Version
    protected int version; */

}

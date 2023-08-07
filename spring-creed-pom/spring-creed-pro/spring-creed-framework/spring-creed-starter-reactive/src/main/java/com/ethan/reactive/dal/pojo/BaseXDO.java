package com.ethan.reactive.dal.pojo;

import com.ethan.reactive.dal.constant.CommonStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * basic Object
 */
@Data
@MappedSuperclass
@Accessors(chain = true)
public abstract class BaseXDO implements Serializable {
    /**
     * 创建时间
     */
    // @CreatedDate TODO 需要使用spring security 框架
    @Column(name = "create_time")
    protected ZonedDateTime createTime = ZonedDateTime.now();
    /**
     * 最后更新时间
     */
    // @LastModifiedDate
    @Column(name = "update_time")
    protected ZonedDateTime updateTime = ZonedDateTime.now();
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
    @Convert(converter = CommonStatusEnum.Converter.class)
    protected CommonStatusEnum enabled= CommonStatusEnum.ENABLE;


    @Version
    protected int version;

}

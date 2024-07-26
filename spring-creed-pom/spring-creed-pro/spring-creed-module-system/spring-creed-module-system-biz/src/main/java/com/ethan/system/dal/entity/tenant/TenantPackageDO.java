package com.ethan.system.dal.entity.tenant;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.converter.SetJacksonConverter;
import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

/**
 * 租户套餐 DO
 *
 */
@Table(name = "system_tenant_package")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantPackageDO extends BaseDO {

    /**
     * 套餐编号，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 套餐名，唯一
     */
    private String name;
    /**
     * 租户套餐状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 关联的菜单编号
     */
    @Convert(converter = SetJacksonConverter.class)
    private Set<Long> menuIds;

}

package com.ethan.system.dal.entity.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.converter.SetJacksonConverter;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.security.websecurity.constant.RoleTypeEnum;
import com.ethan.system.dal.entity.tenant.TenantBaseDO;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 角色 DO
 *
 * @author ruoyi
 */
@Table(name = "system_role")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated(forRemoval = true)
public class RoleDO extends TenantBaseDO {

    /**
     * 角色ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色标识
     * <p>
     * 枚举
     */
    private String code;
    /**
     * 角色排序
     */
    private Integer sort;
    /**
     * 角色状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 角色类型
     * <p>
     * 枚举 {@link RoleTypeEnum}
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;

    /**
     * 数据范围
     * <p>
     * 枚举 {@link DataScopeEnum}
     */
    private Integer dataScope;
    /**
     * 数据范围(指定部门数组)
     * <p>
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    // @TableField(typeHandler = JsonLongSetTypeHandler.class)
    @Convert(converter = SetJacksonConverter.class)
    private Set<Long> dataScopeDeptIds;
}

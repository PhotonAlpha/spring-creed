package com.ethan.example.jpa.dal.permission;

import com.ethan.common.converter.SetJacksonConverter;
import com.ethan.common.pojo.BaseVersioningXDO;
import com.ethan.example.jpa.constant.DataScopeEnum;
import com.ethan.example.jpa.constant.RoleTypeEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "creed_system_roles", indexes = {
        @Index(name = "CSR_IDX_COMMON", columnList = "name")
})
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ToString(exclude = {"groupRoles", "userRoles", "roleAuthorities", "menuRoles"})
public class SystemRoles extends BaseVersioningXDO {
    @Id
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 角色名称
     */
    @Column(nullable = false)
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
     * 角色类型
     * <p>
     * 枚举 {@link RoleTypeEnum}
     */
    @Column
    @Convert(converter = RoleTypeEnum.Converter.class)
    private RoleTypeEnum type = RoleTypeEnum.CUSTOM;
    /**
     * 备注
     */
    private String remark;
    /**
     * 数据范围
     * <p>
     * 枚举 {@link DataScopeEnum}
     */
    @Column
    @Convert(converter = DataScopeEnum.Converter.class)
    private DataScopeEnum dataScope;
    /**
     * 数据范围(指定部门数组)
     * <p>
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    // @TableField(typeHandler = JsonLongSetTypeHandler.class)
    @Convert(converter = SetJacksonConverter.class)
    private Set<Long> dataScopeDeptIds;

    @OneToMany(mappedBy = "roles")
    private List<SystemGroupRoles> groupRoles;

    @OneToMany(mappedBy = "roles")
    private List<SystemUserRoles> userRoles;

    @OneToMany(mappedBy = "roles")
    private List<SystemRoleAuthorities> roleAuthorities;

    @OneToMany(mappedBy = "roles")
    private List<SystemMenuRoles> menuRoles;
}

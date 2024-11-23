package com.ethan.system.dal.entity.permission;

import com.ethan.common.pojo.BaseVersioningXDO;
import com.ethan.example.jpa.constant.RoleTypeEnum;
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

@Entity
@Table(name = "creed_system_authorities", indexes = {
        @Index(name = "CSR_IDX_COMMON", columnList = "authority")
})
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ToString(exclude = {"roleAuthorities", "userAuthorities"})
public class SystemAuthorities extends BaseVersioningXDO {
    @Id
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    /**
     * 权限名称
     */
    private String authority;
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
     * 排序
     */
    private Integer sort;


    @OneToMany(mappedBy = "authorities")
    private List<SystemRoleAuthorities> roleAuthorities;

    @OneToMany(mappedBy = "authorities")
    private List<SystemUserAuthorities> userAuthorities;
}

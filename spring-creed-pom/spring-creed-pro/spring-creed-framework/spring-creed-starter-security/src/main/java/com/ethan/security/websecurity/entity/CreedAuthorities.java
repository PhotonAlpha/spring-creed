package com.ethan.security.websecurity.entity;

import com.ethan.common.converter.SetJacksonConverter;
import com.ethan.common.pojo.BaseXDO;
import com.ethan.security.websecurity.constant.DataScopeEnum;
import com.ethan.security.websecurity.constant.RoleTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "creed_authorities")
@Data
@EqualsAndHashCode
@ToString(exclude = "users")
public class CreedAuthorities extends BaseXDO {

    /**
     * 角色标识
     */
    @Id
    @Column
    @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowflakeId")
    private String id;

    private String authority;
    private String description;
    @Column(length = 1000)
    private String remark;
    /**
     * 角色类型
     * <p>
     * 枚举 {@link RoleTypeEnum}
     */
    @Convert(converter = RoleTypeEnum.Converter.class)
    private RoleTypeEnum type = RoleTypeEnum.SYSTEM;
    /**
     * 数据范围
     * <p>
     * 枚举 {@link DataScopeEnum}
     */
    @Convert(converter = DataScopeEnum.Converter.class)
    private DataScopeEnum dataScope = DataScopeEnum.ALL;
    /**
     * 数据范围(指定部门数组)
     * <p>
     * 适用于 {@link #dataScope} 的值为 {@link DataScopeEnum#DEPT_CUSTOM} 时
     */
    @Convert(converter = SetJacksonConverter.class)
    private Set<Long> dataScopeDeptIds = Collections.emptySet();
    /**
     * 角色排序
     */
    private Integer sort = 0;

    @ManyToMany(mappedBy = "authorities") //配置多表关系
    @JsonIgnore
    private List<CreedUser> users = new ArrayList<>();


/*     @OneToMany(targetEntity = CreedConsumerAuthorities.class, mappedBy = "authorities",
            orphanRemoval = true)
    @JsonIgnore
    private List<CreedConsumerAuthorities> consumerAuthorities = new ArrayList<>(); */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreedAuthorities that = (CreedAuthorities) o;
        return Objects.equals(id, that.id) && Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authority);
    }
}

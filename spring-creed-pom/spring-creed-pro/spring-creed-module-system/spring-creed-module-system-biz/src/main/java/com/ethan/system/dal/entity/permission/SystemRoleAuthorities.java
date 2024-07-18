package com.ethan.system.dal.entity.permission;

import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "creed_system_role_authorities", indexes = {
        @Index(name = "CSRA_IDX_COMMON", columnList = "role_id,authority_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"roles", "authorities"})
@NoArgsConstructor
public class SystemRoleAuthorities extends BaseDO {
    @Id
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotFound(action = NotFoundAction.IGNORE)
    // 解决jpa cannot be mapped as LAZY as its associated entity is defined with @SoftDelete 问题
    // @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) 软删除，不需要级联操作
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private SystemRoles roles;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id", referencedColumnName = "id")
    private SystemAuthorities authorities;

    public SystemRoleAuthorities(SystemRoles roles, SystemAuthorities authorities) {
        this.roles = roles;
        this.authorities = authorities;
    }
}

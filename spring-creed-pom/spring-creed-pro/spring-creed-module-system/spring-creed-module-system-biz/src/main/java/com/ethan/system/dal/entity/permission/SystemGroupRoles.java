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
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "creed_system_group_roles", indexes = {
        @Index(name = "CSGR_IDX_COMMON", columnList = "group_id,role_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"groups", "roles"})
public class SystemGroupRoles extends BaseDO {
    @Id
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotFound(action = NotFoundAction.IGNORE)
    // @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) 软删除，不需要级联操作
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private SystemGroups groups;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private SystemRoles roles;
}

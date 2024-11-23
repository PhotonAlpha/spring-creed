package com.ethan.system.dal.entity.permission;

import com.ethan.common.pojo.BaseVersioningXDO;
import jakarta.persistence.Column;
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
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Table(name = "creed_system_groups", indexes = {
        @Index(name = "CSG_IDX_COMMON", columnList = "groupName")
})
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = false ,exclude = {})
@ToString(exclude = {"groupUsers", "groupRoles"})
public class SystemGroups extends BaseVersioningXDO {
    @Id
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    /**
     * 组名
     */
    @Column(nullable = false, name = "group_name")
    private String groupName;
    private String remark;
    private String avatar;
    private Boolean groupNonLocked;

    @OneToMany(mappedBy = "groups")
    private List<SystemGroupUsers> groupUsers;

    @OneToMany(mappedBy = "groups")
    private List<SystemGroupRoles> groupRoles;

}

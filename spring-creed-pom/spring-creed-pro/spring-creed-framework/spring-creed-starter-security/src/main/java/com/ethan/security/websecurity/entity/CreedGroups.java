package com.ethan.security.websecurity.entity;

import com.ethan.common.pojo.BaseXDO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Table(name = "creed_groups")
@Data
@EqualsAndHashCode
public class CreedGroups extends BaseXDO {
    @Id
    @Column
    @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowflakeId")
    private String id;
    /**
     * 组织名
     */
    private String groupname;

    private Integer parentId = 0;
    private Integer sort = 0;

    private String remark;
    private String email;
    private String phone;
    private String phoneCode;

    @OneToMany(targetEntity = CreedGroupAuthorities.class, mappedBy = "groups", cascade = CascadeType.ALL)
    private List<CreedGroupAuthorities> authorities;

    @OneToMany(targetEntity = CreedGroupMembers.class, mappedBy = "groups", cascade = CascadeType.ALL)
    private List<CreedGroupMembers> members;
}

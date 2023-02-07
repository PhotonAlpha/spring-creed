package com.ethan.security.websecurity.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "creed_group_authorities")
@Data
@EqualsAndHashCode
public class CreedGroupAuthorities extends BaseDO {
    /**
     * 组织名
     */
    @Id
    private String authority;
    private String description;

    private Integer sort = 0;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private CreedGroups groups;

    public CreedGroupAuthorities() {
    }

    public CreedGroupAuthorities(String authority, CreedGroups groups) {
        this.authority = authority;
        this.groups = groups;
    }
    // @OneToMany(targetEntity = CreedGroups.class, mappedBy = "groupAuthorities")
    // private List<CreedGroups> groups;

}

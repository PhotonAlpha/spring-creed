package com.ethan.security.websecurity.entity;

import com.ethan.common.pojo.BaseXDO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "creed_group_members")
@Data
@EqualsAndHashCode
@Deprecated(forRemoval = true)
public class CreedGroupMembers extends BaseXDO {
    @Id
    @Column
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected String id;

    // private String groupId;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private CreedGroups groups;
}

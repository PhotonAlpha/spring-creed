package com.ethan.identity.core.segment.dal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

@Table(name = "creed_system_leaf_alloc")
@Entity
@DynamicUpdate
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SystemLeafAlloc {
    @Id
    @Column(name = "biz_tag")
    private String bizTag;
    @Column(name = "max_id")
    private long maxId;
    private int step;
    private String description;
    @Column(name = "update_time")
    private Instant updateTime;

    public SystemLeafAlloc(String bizTag, long maxId, int step) {
        this.bizTag = bizTag;
        this.maxId = maxId;
        this.step = step;
    }

    public SystemLeafAlloc(String bizTag) {
        this.bizTag = bizTag;
    }
}

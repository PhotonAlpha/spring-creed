package com.ethan.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "creed_authorities")
@Data
@EqualsAndHashCode
@ToString(exclude = "consumerAuthorities")
public class CreedAuthorities extends BaseDO {

    /**
     * 角色标识
     */
    @Id
    @Column
    @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.utils.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowflakeId")
    private String id;

    private String authority;
    private String description;
    /**
     * 角色排序
     */
    private Integer sort = 0;

/*     @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY) //配置多表关系
    // @JoinTable(name = "creed_consumer_authorities",
    //         joinColumns = @JoinColumn(name = "authority", referencedColumnName = "id"),
    //         inverseJoinColumns = @JoinColumn(name = "username", referencedColumnName = "username"))
    private List<CreedConsumer> consumers = new ArrayList<>(); */


    @OneToMany(targetEntity = CreedConsumerAuthorities.class, mappedBy = "authorities",
            orphanRemoval = true)
    private List<CreedConsumerAuthorities> consumerAuthorities = new ArrayList<>();

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

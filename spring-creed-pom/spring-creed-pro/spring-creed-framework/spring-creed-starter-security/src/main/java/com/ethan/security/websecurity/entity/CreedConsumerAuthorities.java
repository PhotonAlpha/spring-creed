package com.ethan.security.websecurity.entity;

import com.ethan.common.pojo.BaseDO;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Entity
@Table(name = "creed_consumer_authorities")
// @IdClass(value = CreedConsumerAuthorities.CreedConsumerAuthoritiesId.class)
@AttributeOverride(name = "deleted", column = @Column(name = "enabled"))
@Data
@EqualsAndHashCode
// @ToString(exclude = "authorities")
public class CreedConsumerAuthorities extends BaseDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public CreedConsumerAuthorities() {
    }

    public CreedConsumerAuthorities(CreedConsumer consumer, CreedAuthorities authorities) {
        this.consumer = consumer;
        this.authorities = authorities;
    }


    // @Id
    @ManyToOne
    @JoinColumn(name = "consumer_id", referencedColumnName = "id")
    private CreedConsumer consumer;

    // @Id
    @ManyToOne
    @JoinColumn(name = "authority_id", referencedColumnName = "id")
    private CreedAuthorities authorities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreedConsumerAuthorities that = (CreedConsumerAuthorities) o;
        return Objects.equals(id, that.id) && Objects.equals(consumer, that.consumer) && Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, consumer, authorities);
    }
}

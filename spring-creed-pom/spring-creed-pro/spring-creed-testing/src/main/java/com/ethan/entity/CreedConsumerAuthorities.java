package com.ethan.entity;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.constant.SexEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "creed_consumer_authorities")
// @IdClass(value = CreedConsumerAuthorities.CreedConsumerAuthoritiesId.class)
@Data
@EqualsAndHashCode
// @ToString(exclude = "authorities")
public class CreedConsumerAuthorities {
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

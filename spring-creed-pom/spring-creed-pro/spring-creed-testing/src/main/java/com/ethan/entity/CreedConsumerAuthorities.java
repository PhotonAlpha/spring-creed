package com.ethan.entity;

import com.ethan.listener.CreedConsumerAuthoritiesEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // 返回类型定义
    @DomainEvents
    public List<Object> domainEvents(){
        System.out.println("CreedConsumerAuthoritiesEvent domainEvents");
        return Stream.of(new CreedConsumerAuthoritiesEvent(this)).collect(Collectors.toList());
    }
    // 事件发布后callback
    @AfterDomainEventPublication
    void callback() {
        System.err.println("CreedConsumerAuthoritiesEvent ok");
    }
}

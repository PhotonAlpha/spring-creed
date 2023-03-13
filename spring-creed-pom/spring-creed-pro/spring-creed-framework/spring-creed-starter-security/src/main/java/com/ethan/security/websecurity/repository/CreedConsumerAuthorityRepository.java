package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedConsumerAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface CreedConsumerAuthorityRepository extends JpaRepository<CreedConsumerAuthorities, Long> {
    List<CreedConsumerAuthorities> findByConsumerId(String s);

    List<CreedConsumerAuthorities> findByAuthoritiesIdIn(Collection<String> roleIds);

    long countByUpdateTimeGreaterThan(Instant maxUpdateTime);

    void deleteByConsumerIdAndAuthoritiesIdIn(String userId, Collection<String> deleteRoleIds);

    void deleteByAuthoritiesId(String roleId);

    void deleteByConsumerId(String userId);
}

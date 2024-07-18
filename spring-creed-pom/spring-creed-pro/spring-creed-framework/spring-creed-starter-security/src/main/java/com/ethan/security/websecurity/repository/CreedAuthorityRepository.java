package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Deprecated(forRemoval = true)
public interface CreedAuthorityRepository extends JpaRepository<CreedAuthorities, String>, JpaSpecificationExecutor<CreedAuthorities> {
    Optional<CreedAuthorities> findByAuthority(String s);

    long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);

    List<CreedAuthorities> findByEnabledIn(Collection<Integer> statuses);

}

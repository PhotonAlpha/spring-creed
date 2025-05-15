package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedGroupAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated(forRemoval = true)
public interface CreedGroupsAuthoritiesRepository extends JpaRepository<CreedGroupAuthorities, String> {
}

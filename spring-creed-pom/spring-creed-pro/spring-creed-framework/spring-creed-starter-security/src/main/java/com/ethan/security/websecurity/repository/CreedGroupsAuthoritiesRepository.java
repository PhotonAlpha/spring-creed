package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedGroupAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreedGroupsAuthoritiesRepository extends JpaRepository<CreedGroupAuthorities, String> {
}

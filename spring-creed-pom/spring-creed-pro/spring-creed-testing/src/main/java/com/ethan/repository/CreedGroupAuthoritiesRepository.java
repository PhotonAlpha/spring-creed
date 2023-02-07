package com.ethan.repository;

import com.ethan.entity.CreedGroupAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreedGroupAuthoritiesRepository extends JpaRepository<CreedGroupAuthorities, String> {
}

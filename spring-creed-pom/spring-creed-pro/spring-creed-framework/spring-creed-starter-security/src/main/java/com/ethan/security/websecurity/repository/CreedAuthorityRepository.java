package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedAuthorityRepository extends JpaRepository<CreedAuthorities, String> {
    Optional<CreedAuthorities> findByAuthority(String s);
}

package com.ethan.repository;

import com.ethan.entity.CreedAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedAuthorityRepository extends JpaRepository<CreedAuthorities, String> {
    Optional<CreedAuthorities> findByAuthority(String s);
}

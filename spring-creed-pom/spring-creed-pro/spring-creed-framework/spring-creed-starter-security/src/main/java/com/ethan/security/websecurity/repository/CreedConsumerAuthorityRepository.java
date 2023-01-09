package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedConsumerAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreedConsumerAuthorityRepository extends JpaRepository<CreedConsumerAuthorities, Long> {
    Optional<List<CreedConsumerAuthorities>> findByConsumerId(String s);
}

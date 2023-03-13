package com.ethan.security.oauth2.repository;

import com.ethan.security.oauth2.entity.CreedOAuth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface CreedOAuth2RegisteredClientRepository extends JpaRepository<CreedOAuth2RegisteredClient, String> {

    Optional<CreedOAuth2RegisteredClient> findByClientId(String clientId);

    long countByUpdateTimeGreaterThan(Instant maxUpdateTime);
}

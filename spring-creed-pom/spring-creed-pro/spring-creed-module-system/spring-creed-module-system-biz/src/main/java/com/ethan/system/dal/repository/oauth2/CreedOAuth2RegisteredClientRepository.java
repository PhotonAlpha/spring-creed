package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
public interface CreedOAuth2RegisteredClientRepository extends JpaRepository<CreedOAuth2RegisteredClient, String> {

    Optional<CreedOAuth2RegisteredClient> findByClientId(String clientId);

    long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);
}

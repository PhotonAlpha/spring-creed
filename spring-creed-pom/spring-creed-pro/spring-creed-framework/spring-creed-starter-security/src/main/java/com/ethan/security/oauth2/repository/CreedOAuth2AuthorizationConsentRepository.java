package com.ethan.security.oauth2.repository;

import com.ethan.security.oauth2.entity.CreedOAuth2AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedOAuth2AuthorizationConsentRepository extends JpaRepository<CreedOAuth2AuthorizationConsent, CreedOAuth2AuthorizationConsent.AuthorizationConsentId> {

    Optional<CreedOAuth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

}

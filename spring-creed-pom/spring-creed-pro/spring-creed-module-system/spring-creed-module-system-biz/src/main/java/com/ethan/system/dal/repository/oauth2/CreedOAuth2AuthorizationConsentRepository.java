package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.dal.entity.oauth2.CreedOAuth2AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedOAuth2AuthorizationConsentRepository extends JpaRepository<CreedOAuth2AuthorizationConsent, CreedOAuth2AuthorizationConsent.AuthorizationConsentId> {

    Optional<CreedOAuth2AuthorizationConsent> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    void deleteByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

}

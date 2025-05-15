/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.oauth2.client;

import com.ethan.system.dal.entity.oauth2.client.CreedOAuth2ClientConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedOAuth2ClientConfigurationRepository extends JpaRepository<CreedOAuth2ClientConfiguration, Long>, JpaSpecificationExecutor<CreedOAuth2ClientConfiguration> {
    Optional<CreedOAuth2ClientConfiguration> findByRegistrationIdAndClientId(String registrationId, String clientId);
}

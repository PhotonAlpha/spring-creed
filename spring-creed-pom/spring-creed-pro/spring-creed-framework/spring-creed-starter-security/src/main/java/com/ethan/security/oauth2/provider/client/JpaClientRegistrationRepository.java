/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.provider.client;

import com.ethan.security.oauth2.entity.client.CreedOAuth2ClientConfiguration;
import com.ethan.security.oauth2.repository.client.CreedOAuth2ClientConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Deprecated(forRemoval = true)
public class JpaClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration>, InitializingBean {
    private Map<String, ClientRegistration> registrations;
    private final CreedOAuth2ClientConfigurationRepository clientConfigurationRepository;

    private static final List<AuthorizationGrantType> AUTHORIZATION_GRANT_TYPES = Arrays.asList(
            AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.CLIENT_CREDENTIALS,
            AuthorizationGrantType.REFRESH_TOKEN, AuthorizationGrantType.JWT_BEARER, AuthorizationGrantType.DEVICE_CODE);
    private static final List<ClientAuthenticationMethod> CLIENT_AUTHENTICATION_METHOD_TYPES = Arrays.asList(
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC, ClientAuthenticationMethod.CLIENT_SECRET_POST,
            ClientAuthenticationMethod.CLIENT_SECRET_JWT, ClientAuthenticationMethod.PRIVATE_KEY_JWT);

    public JpaClientRegistrationRepository(CreedOAuth2ClientConfigurationRepository clientConfigurationRepository) {
        this.registrations = Collections.emptyMap();
        this.clientConfigurationRepository = clientConfigurationRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initLocalCache();
    }

    public void initLocalCache() {
        List<CreedOAuth2ClientConfiguration> configurationList = clientConfigurationRepository.findAll();
        List<ClientRegistration> registrationList = convert(configurationList);
        this.registrations = createRegistrationsMap(registrationList);
        log.info("[initLocalCache][初始化 ClientRegistration 数量为 {}]", this.registrations.size());
    }

    private ClientRegistration convert(CreedOAuth2ClientConfiguration bean) {
        String authenticationMethod = bean.getClientAuthenticationMethod();
        String authorizationGrantType = bean.getAuthorizationGrantType();
        ClientAuthenticationMethod clientAuthenticationMethod = CLIENT_AUTHENTICATION_METHOD_TYPES.stream().filter(t -> StringUtils.equals(t.getValue(), authenticationMethod))
                .findFirst().orElse(ClientAuthenticationMethod.NONE);
        AuthorizationGrantType grantType = AUTHORIZATION_GRANT_TYPES.stream().filter(t -> StringUtils.equals(t.getValue(), authorizationGrantType))
                .findFirst().orElse(AuthorizationGrantType.CLIENT_CREDENTIALS);
        return ClientRegistration.withRegistrationId(bean.getRegistrationId())
                .tokenUri(bean.getTokenUri())
                .clientId(bean.getClientId())
                .clientSecret(bean.getClientSecret())
                .scope(bean.getScopes())
                .clientAuthenticationMethod(clientAuthenticationMethod)
                .authorizationGrantType(grantType)
                .build();
    }
    private List<ClientRegistration> convert(List<CreedOAuth2ClientConfiguration> bean) {
        if (!CollectionUtils.isEmpty(bean)) {
            return bean.stream().map(this::convert).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }



    /**
     * Constructs an {@code InMemoryClientRegistrationRepository} using the provided
     * {@code Map} of {@link ClientRegistration#getRegistrationId() registration id} to
     * {@link ClientRegistration}.
     * @param registrations the {@code Map} of client registration(s)
     * @since 5.2
     */
    public JpaClientRegistrationRepository(Map<String, ClientRegistration> registrations) {
        Assert.notNull(registrations, "registrations cannot be null");
        this.registrations = registrations;
        this.clientConfigurationRepository = null;
    }



    private static Map<String, ClientRegistration> createRegistrationsMap(List<ClientRegistration> registrations) {
        Assert.notEmpty(registrations, "registrations cannot be empty");
        return toUnmodifiableConcurrentMap(registrations);
    }

    private static Map<String, ClientRegistration> toUnmodifiableConcurrentMap(List<ClientRegistration> registrations) {
        ConcurrentHashMap<String, ClientRegistration> result = new ConcurrentHashMap<>();
        for (ClientRegistration registration : registrations) {
            Assert.state(!result.containsKey(registration.getRegistrationId()),
                    () -> String.format("Duplicate key %s", registration.getRegistrationId()));
            result.put(registration.getRegistrationId(), registration);
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return this.registrations.values().iterator();
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        Assert.hasText(registrationId, "registrationId cannot be empty");
        return this.registrations.get(registrationId);
    }
}

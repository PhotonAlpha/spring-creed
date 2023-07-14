/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.security.oauth2.provider;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class JpaClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {
    private final Map<String, ClientRegistration> registrations;

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
    }

    /**
     * Constructs an {@code InMemoryClientRegistrationRepository} using the provided
     * parameters.
     * @param registrations the client registration(s)
     */
    public JpaClientRegistrationRepository(ClientRegistration... registrations) {
        this(Arrays.asList(registrations));
    }

    /**
     * Constructs an {@code InMemoryClientRegistrationRepository} using the provided
     * parameters.
     * @param registrations the client registration(s)
     */
    public JpaClientRegistrationRepository(List<ClientRegistration> registrations) {
        this(createRegistrationsMap(registrations));
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

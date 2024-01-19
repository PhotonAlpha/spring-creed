package com.ethan.security.oauth2.provider;

import com.ethan.security.oauth2.entity.CreedOAuth2RegisteredClient;
import com.ethan.security.oauth2.repository.CreedOAuth2RegisteredClientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class JpaRegisteredClientRepository implements RegisteredClientRepository {
    private final CreedOAuth2RegisteredClientRepository clientRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JpaRegisteredClientRepository(CreedOAuth2RegisteredClientRepository clientRepository) {
        Assert.notNull(clientRepository, "clientRepository cannot be null");
        this.clientRepository = clientRepository;

        ClassLoader classLoader = JpaRegisteredClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        RegisteredClient existingRegisteredClient = findById(registeredClient.getId());
        if (existingRegisteredClient != null) {
            clientRepository.save(updateRegisteredClient(registeredClient));
        } else {
            clientRepository.save(toEntity(registeredClient));
        }
    }

    /**
     * since upgrade authorization server to 2.0.x, updateRegisteredClient add
     * {@see ClientSecretAuthenticationProvider#authenticate(Authentication authentication)}
     * this.passwordEncoder.upgradeEncoding(registeredClient.getClientSecret())
     *
     * @param registeredClient
     * @return
     */

    private CreedOAuth2RegisteredClient updateRegisteredClient(RegisteredClient registeredClient) {
        CreedOAuth2RegisteredClient entity = this.clientRepository.findById(registeredClient.getId()).orElse(null);
        Assert.notNull(entity, "registeredClient not exist, can not update.");

        List<String> clientAuthenticationMethods = Optional.of(registeredClient)
                .map(RegisteredClient::getClientAuthenticationMethods)
                .orElse(Collections.emptySet())
                .stream()
                .map(ClientAuthenticationMethod::getValue)
                .toList();

        List<String> authorizationGrantTypes   = Optional.of(registeredClient)
                .map(RegisteredClient::getAuthorizationGrantTypes)
                .orElse(Collections.emptySet())
                .stream()
                .map(AuthorizationGrantType::getValue)
                .toList();

        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        entity.setAuthorizationGrantTypes(authorizationGrantTypes);
        entity.setRedirectUris(registeredClient.getRedirectUris());
        entity.setPostLogoutRedirectUris(registeredClient.getPostLogoutRedirectUris());
        entity.setScopes(registeredClient.getScopes());
        entity.setClientSettings(registeredClient.getClientSettings().getSettings());
        entity.setTokenSettings(registeredClient.getTokenSettings().getSettings());
        return entity;
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return this.clientRepository.findById(id).map(this::toObject).orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        return this.clientRepository.findByClientId(clientId).map(this::toObject).orElse(null);
    }

    private CreedOAuth2RegisteredClient toEntity(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        List<String> clientAuthenticationMethods = Optional.of(registeredClient)
                .map(RegisteredClient::getClientAuthenticationMethods)
                .orElse(Collections.emptySet())
                .stream()
                .map(ClientAuthenticationMethod::getValue)
                .toList();

        List<String> authorizationGrantTypes   = Optional.of(registeredClient)
                .map(RegisteredClient::getAuthorizationGrantTypes)
                .orElse(Collections.emptySet())
                .stream()
                .map(AuthorizationGrantType::getValue)
                .toList();

        CreedOAuth2RegisteredClient entity = new CreedOAuth2RegisteredClient();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt());
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt());
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        entity.setAuthorizationGrantTypes(authorizationGrantTypes);
        entity.setRedirectUris(registeredClient.getRedirectUris());
        entity.setPostLogoutRedirectUris(registeredClient.getPostLogoutRedirectUris());
        entity.setScopes(registeredClient.getScopes());
        entity.setClientSettings(registeredClient.getClientSettings().getSettings());
        entity.setTokenSettings(registeredClient.getTokenSettings().getSettings());
        return entity;
    }

    @SneakyThrows
    private String writeMap(Map<String, Object> settings) {
        return objectMapper.writeValueAsString(settings);
    }

    private RegisteredClient toObject(CreedOAuth2RegisteredClient client) {
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(
                client.getClientAuthenticationMethods());
        List<String> authorizationGrantTypes = client.getAuthorizationGrantTypes();
        Set<String> redirectUris = client.getRedirectUris();
        Set<String> postLogoutRedirectUris = client.getPostLogoutRedirectUris();
        Set<String> clientScopes = client.getScopes();

        RegisteredClient.Builder builder = RegisteredClient.withId(client.getId())
                .clientId(client.getClientId())
                .clientIdIssuedAt(client.getClientIdIssuedAt())
                .clientSecret(client.getClientSecret())
                .clientSecretExpiresAt(client.getClientSecretExpiresAt())
                .clientName(client.getClientName())
                .clientAuthenticationMethods(authenticationMethods ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes(grantTypes ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(resolveAuthorizationGrantType(grantType))))
                .redirectUris(uris -> uris.addAll(redirectUris))
                .postLogoutRedirectUris(uris -> uris.addAll(postLogoutRedirectUris))
                .scopes(scopes -> scopes.addAll(clientScopes));
        // Map<String, Object> clientSettingsMap = parseMap(client.getClientSettings());
        builder.clientSettings(ClientSettings.withSettings(client.getClientSettings()).build());

        Map<String, Object> tokenSettingsMap = client.getTokenSettings();
        TokenSettings.Builder tokenSettingsBuilder = TokenSettings.withSettings(tokenSettingsMap);
        if (!tokenSettingsMap.containsKey(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT)) {
            tokenSettingsBuilder.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED);
        }
        builder.tokenSettings(tokenSettingsBuilder.build());
        // context.getRegisteredClient().getTokenSettings().getAccessTokenFormat()
        return builder.build();
    }

    private ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(clientAuthenticationMethod);      // Custom client authentication method
    }

    private AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(authorizationGrantType);
    }

    @SneakyThrows
    private Map<String, Object> parseMap(String clientSettings) {
        return this.objectMapper.readValue(clientSettings, new TypeReference<Map<String, Object>>() {
        });

    }

}

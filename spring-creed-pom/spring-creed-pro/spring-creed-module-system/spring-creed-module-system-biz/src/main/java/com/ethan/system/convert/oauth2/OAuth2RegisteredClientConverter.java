/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.convert.oauth2;

import com.ethan.system.dal.registration.JpaRegisteredClientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OAuth2RegisteredClientConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TypeReference<Map<String, Object>> typeReference =new TypeReference<>() {
    };
    private static final Logger log = LoggerFactory.getLogger(OAuth2RegisteredClientConverter.class);

    public OAuth2RegisteredClientConverter() {
        ClassLoader classLoader = JpaRegisteredClientRepository.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (Objects.isNull(attribute)) {
            log.warn("trying to convertToDatabaseColumn attribute:{} is NULL", attribute);
            return null;
        }
        log.info("trying to convertToDatabaseColumn clazz:{} attribute:{}", attribute.getClass(), attribute);
        return objectMapper.writeValueAsString(attribute);
    }

    @Override
    @SneakyThrows
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        log.info("trying to convertToEntityAttribute clazz:{} dbData:{}", typeReference.getType().getTypeName(), dbData);
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        return objectMapper.readValue(dbData, typeReference);
    }
}

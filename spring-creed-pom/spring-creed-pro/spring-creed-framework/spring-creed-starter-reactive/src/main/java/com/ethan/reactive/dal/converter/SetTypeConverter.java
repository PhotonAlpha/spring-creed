package com.ethan.reactive.dal.converter;

import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetTypeConverter implements AttributeConverter<Set<String>, String> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        log.info("trying to convertToEntityAttribute dbData:{}", dbData);
        if (StringUtils.isBlank(dbData)) {
            return Collections.emptySet();
        }
        return Stream.of(StringUtils.split(dbData, ",")).collect(Collectors.toSet());
    }

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (CollectionUtils.isEmpty(attribute)) {
            log.warn("trying to convertToDatabaseColumn attribute:{} is NULL", attribute);
            return null;
        }
        log.info("trying to convertToDatabaseColumn attribute:{}", attribute);
        return StringUtils.join(attribute, ",");
    }
}

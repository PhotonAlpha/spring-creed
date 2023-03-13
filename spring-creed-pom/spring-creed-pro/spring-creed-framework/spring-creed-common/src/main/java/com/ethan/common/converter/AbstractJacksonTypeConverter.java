package com.ethan.common.converter;

import com.ethan.common.utils.json.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

// @Converter(autoApply = true)
public abstract class AbstractJacksonTypeConverter<T> implements AttributeConverter<T, String> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final TypeReference<T> typeReference ;

    protected AbstractJacksonTypeConverter(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (Objects.isNull(attribute)) {
            log.warn("trying to convertToDatabaseColumn attribute:{} is NULL", attribute);
            return null;
        }
        log.info("trying to convertToDatabaseColumn clazz:{} attribute:{}", attribute.getClass(), attribute);
        return JacksonUtils.toJsonString(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        log.info("trying to convertToEntityAttribute clazz:{} dbData:{}", typeReference.getType().getTypeName(), dbData);
        if (StringUtils.isBlank(dbData)) {
            return null;
        }
        return JacksonUtils.parseObject(dbData, typeReference);
    }
}

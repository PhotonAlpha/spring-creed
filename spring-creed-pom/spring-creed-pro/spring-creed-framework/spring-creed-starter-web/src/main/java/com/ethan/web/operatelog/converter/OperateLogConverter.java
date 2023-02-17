/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.operatelog.converter;

import com.ethan.common.utils.json.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class OperateLogConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        return jsonStr(attribute);
    }

    private String jsonStr(Map<String, Object> attribute) {
        return JacksonUtils.toJsonString(attribute);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        return mapFromJson(dbData);
    }

    private Map<String, Object> mapFromJson(String dbData) {
        TypeReference<Map<String, Object>> reference = new TypeReference<>() {
        };
        return JacksonUtils.parseObject(dbData, reference);
    }
}

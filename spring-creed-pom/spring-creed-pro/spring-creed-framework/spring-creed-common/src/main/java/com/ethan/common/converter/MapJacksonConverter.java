/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.common.converter;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

public class MapJacksonConverter extends AbstractJacksonTypeConverter<Map<String, Object>> {
    public MapJacksonConverter() {
        super(new TypeReference<>() {
        });
    }
}

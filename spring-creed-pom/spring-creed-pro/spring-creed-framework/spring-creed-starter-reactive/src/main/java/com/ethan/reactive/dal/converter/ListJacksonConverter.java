/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.reactive.dal.converter;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class ListJacksonConverter extends AbstractJacksonTypeConverter<List<String>> {
    public ListJacksonConverter() {
        super(new TypeReference<>() {
        });
    }
}

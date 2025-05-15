/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.common.converter;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

public class SetJacksonConverter extends AbstractJacksonTypeConverter<Set<Long>> {
    public SetJacksonConverter() {
        super(new TypeReference<>() {
        });
    }
}

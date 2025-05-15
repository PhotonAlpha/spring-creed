package com.ethan.security.websecurity.constant;

import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum AuthorityTypeEnum implements PersistEnum2DB<Integer> {

    /**
     * 内置权限
     */
    SYSTEM(1),
    /**
     * 自定义权限
     */
    CUSTOM(2);

    private final Integer type;


    @Override
    @JsonValue
    public Integer getData() {
        return type;
    }

    @JsonCreator
    public static AuthorityTypeEnum findByType(Integer type) {
        return Stream.of(values()).filter(e -> e.getType().equals(type)).findFirst().orElse(CUSTOM);
    }

    public static class Converter extends AbstractEnumConverter<AuthorityTypeEnum, Integer> {
        public Converter() {
            super(AuthorityTypeEnum.class);
        }
    }
}

package com.ethan.common.constant;

import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.stream.Stream;

public enum SexEnum implements PersistEnum2DB<Integer> {
    /** 男 */
    MALE(1),
    /** 女 */
    FEMALE(2),
    /* 未知 */
    UNKNOWN(3);
    /**
     * 性别
     */
    private Integer sex;

    SexEnum(Integer sex) {
        this.sex = sex;
    }

    @JsonCreator //序列化的时候，标记此转换方式
    public static SexEnum findByValue(Integer value) {
        return Stream.of(values()).filter(e -> Objects.equals(e.getSex(), value))
                .findFirst().orElse(UNKNOWN);
    }

    public Integer getSex() {
        return sex;
    }

    @Override
    @JsonValue //反序列化的时候，标记此转换方式
    public Integer getData() {
        return sex;
    }

    public static class Converter extends AbstractEnumConverter<SexEnum, Integer> {
        public Converter() {
            super(SexEnum.class);
        }
    }
}

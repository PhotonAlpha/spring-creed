package com.ethan.common.constant;

import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;

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

    public static SexEnum findByValue(Integer value) {
        return Stream.of(values()).filter(e -> Objects.equals(e.getSex(), value))
                .findFirst().orElse(UNKNOWN);
    }

    public Integer getSex() {
        return sex;
    }

    @Override
    public Integer getData() {
        return sex;
    }

    public static class Converter extends AbstractEnumConverter<SexEnum, Integer> {
        public Converter() {
            super(SexEnum.class);
        }
    }
}

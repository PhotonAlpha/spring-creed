package com.ethan.common.constant;

import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum CommonStatusEnum implements IntArrayValuable, PersistEnum2DB<Integer> {
    ENABLE(0, "开启"),
    DISABLE(1, "关闭");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CommonStatusEnum::getStatus).toArray();

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    CommonStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    @JsonCreator //序列化的时候，标记此转换方式
    public static CommonStatusEnum convert(int status) {
        return Arrays.stream(values())
                .filter(e -> e.status == status)
                .findFirst()
                .orElse(DISABLE);
    }
    public static CommonStatusEnum convert(boolean enabled) {
        return Boolean.TRUE.equals(enabled) ? ENABLE : DISABLE;
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }


    @Override
    @JsonValue //反序列化的时候，标记此转换方式
    public Integer getData() {
        return status;
    }

    public boolean enabled() {
        if (status == 0)
            return true;
        return false;
    }

    public static class Converter extends AbstractEnumConverter<CommonStatusEnum, Integer> {
        public Converter() {
            super(CommonStatusEnum.class);
        }
    }
}

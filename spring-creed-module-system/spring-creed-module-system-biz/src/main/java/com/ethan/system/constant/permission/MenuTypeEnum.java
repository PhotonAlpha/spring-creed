package com.ethan.system.constant.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.constant.IntArrayValuable;
import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 菜单类型枚举类
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum implements IntArrayValuable, PersistEnum2DB<Integer> {

    DIR(1), // 目录
    MENU(2), // 菜单
    BUTTON(3) // 按钮
    ;
    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(MenuTypeEnum::getType).toArray();
    /**
     * 类型
     */
    private final Integer type;

    public static MenuTypeEnum convert(Integer code) {
        return Arrays.stream(values())
                .filter(e -> Objects.equals(e.type, code))
                .findFirst()
                .orElse(MENU);
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }

    @Override
    public Integer getData() {
        return type;
    }


    public static class Converter extends AbstractEnumConverter<MenuTypeEnum, Integer> {
        public Converter() {
            super(MenuTypeEnum.class);
        }
    }
}

package com.ethan.example.jpa.constant;

import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * 数据范围枚举类
 *
 * 用于实现数据级别的权限
 *
 * 
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum implements PersistEnum2DB<Integer> {

    ALL(1), // 全部数据权限

    DEPT_CUSTOM(2), // 指定部门数据权限
    DEPT_ONLY(3), // 部门数据权限
    DEPT_AND_CHILD(4), // 部门及以下数据权限

    SELF(5); // 仅本人数据权限

    /**
     * 范围
     */
    private final Integer scope;

    public static DataScopeEnum findByDataScope(Integer dataScope) {
        return Stream.of(values()).filter(e -> e.getScope().equals(dataScope)).findFirst().orElse(null);
    }


    @Override
    public Integer getData() {
        return scope;
    }

    public static class Converter extends AbstractEnumConverter<DataScopeEnum, Integer> {
        public Converter() {
            super(DataScopeEnum.class);
        }
    }
}

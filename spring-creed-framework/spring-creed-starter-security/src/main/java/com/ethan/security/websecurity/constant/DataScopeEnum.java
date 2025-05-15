package com.ethan.security.websecurity.constant;

import com.ethan.common.converter.AbstractEnumConverter;
import com.ethan.common.converter.PersistEnum2DB;
import com.ethan.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;

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

    @JsonCreator //序列化的时候，标记此转换方式
    public static DataScopeEnum findByDataScope(Integer dataScope) {
        return Stream.of(values()).filter(e -> e.getScope().equals(dataScope)).findFirst().orElseThrow(() -> exception(1000000000, "Invalid DataScope"));
    }


    @Override
    @JsonValue //反序列化的时候，标记此转换方式
    public Integer getData() {
        return scope;
    }

    public static class Converter extends AbstractEnumConverter<DataScopeEnum, Integer> {
        public Converter() {
            super(DataScopeEnum.class);
        }
    }
}

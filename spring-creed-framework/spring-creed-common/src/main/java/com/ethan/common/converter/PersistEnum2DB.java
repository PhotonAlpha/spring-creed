package com.ethan.common.converter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface PersistEnum2DB<DB> {
    //反序列化的时候，标记此转换方式
    @JsonValue
    DB getData();
}

package com.ethan.common.converter;

import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public abstract class AbstractEnumConverter<A extends Enum<A> & PersistEnum2DB<D>, D> implements AttributeConverter<A, D> {
    private final Class<A> clazz;

    protected AbstractEnumConverter(Class<A> clazz) {
        this.clazz = clazz;
    }

    @Override
    public D convertToDatabaseColumn(A attribute) {
        return Objects.nonNull(attribute) ? attribute.getData() : null;
    }

    @Override
    public A convertToEntityAttribute(D dbData) {
        if (Objects.isNull(dbData) || (dbData instanceof String str && StringUtils.isBlank(str))) {
            return null;
        }
        A[] enums = clazz.getEnumConstants();
        for (A e : enums) {
            if (e.getData().equals(dbData)) {
                return e;
            }
        }
        throw new UnsupportedOperationException(String.format("枚举化异常。枚举【%s】,数据库中的值为【%s】", clazz.getSimpleName(), dbData));
    }
}

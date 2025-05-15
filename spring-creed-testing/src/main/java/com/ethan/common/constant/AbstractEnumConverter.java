package com.ethan.common.constant;

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

public abstract class AbstractEnumConverter<ATTR extends Enum<ATTR> & PersistEnum2DB<DB>, DB> implements AttributeConverter<ATTR, DB> {
    private final Class<ATTR> clazz;

    public AbstractEnumConverter(Class<ATTR> clazz) {
        this.clazz = clazz;
    }

    @Override
    public DB convertToDatabaseColumn(ATTR attribute) {
        return Objects.nonNull(attribute) ? attribute.getData() : null;
    }

    @Override
    public ATTR convertToEntityAttribute(DB dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        ATTR[] enums = clazz.getEnumConstants();
        for (ATTR e : enums) {
            if (e.getData().equals(dbData)) {
                return e;
            }
        }
        throw new UnsupportedOperationException(String.format("枚举化异常。枚举【%s】,数据库中的值为【%s】", clazz.getSimpleName(), dbData));
    }
}

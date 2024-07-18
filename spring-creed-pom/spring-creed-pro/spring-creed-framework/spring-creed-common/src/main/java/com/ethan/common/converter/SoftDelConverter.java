package com.ethan.common.converter;

import com.ethan.common.constant.CommonStatusEnum;
import jakarta.persistence.AttributeConverter;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 7/16/24
 * Using a different SoftDeleteType
 * Hibernate supports 2 different SoftDeleteTypes that determine the meaning of the boolean stored in the database:
 *
 *  1. SoftDeleteType.ACTIVE
 *      The value true marks a record as active, and Hibernate uses active as the default column name.
 *  2. SoftDeleteType.DELETED
 *      The value true marks a record as deleted, and Hibernate uses deleted as the default column name. This is the default.
 */
public class SoftDelConverter implements AttributeConverter<Boolean, Integer> {
/*     @Override
    public CommonStatusEnum convertToDatabaseColumn(Boolean attribute) {
        return Boolean.TRUE.equals(attribute) ? CommonStatusEnum.ENABLE : CommonStatusEnum.DISABLE;
    }

    @Override
    public Boolean convertToEntityAttribute(CommonStatusEnum dbData) {
        return CommonStatusEnum.ENABLE.equals(dbData);
    } */

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        return Boolean.TRUE.equals(attribute) ? CommonStatusEnum.ENABLE.getData() : CommonStatusEnum.DISABLE.getData();
    }
    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        return CommonStatusEnum.ENABLE.equals(CommonStatusEnum.convert(dbData));
    }
}

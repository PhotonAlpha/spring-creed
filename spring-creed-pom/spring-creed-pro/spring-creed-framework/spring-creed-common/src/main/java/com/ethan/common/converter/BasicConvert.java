package com.ethan.common.converter;

import com.ethan.common.constant.SexEnum;
import com.ethan.common.utils.date.DateUtils;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 7/24/24
 */
public interface BasicConvert {
    default LocalDateTime map(Instant instant) {
        return DateUtils.instant2LocalDateTime(instant);
    }
    default Instant map(LocalDateTime time) {
        return DateUtils.localDateTime2Instant(time);
    }

    default SexEnum map(Integer code) {
        return SexEnum.findByValue(code);
    }
    default Integer map(SexEnum sex) {
        return sex.getData();
    }
}

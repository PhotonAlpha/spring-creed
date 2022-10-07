package com.ethan.std.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/18/2022 4:58 PM
 */
public class DateUtil {
    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);
    public static boolean expired(String timestamp, long nonceTimeoutSeconds) {
        ZonedDateTime requestTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(timestamp)), TimeZone.getDefault().toZoneId());
        ZonedDateTime expiredTime = requestTime.plus(nonceTimeoutSeconds, ChronoUnit.SECONDS);
        ZonedDateTime now = ZonedDateTime.now();
        log.info("request time:{} expiredTime:{} now:{}", requestTime, expiredTime, now);
        return now.isBefore(expiredTime) && now.isAfter(requestTime);
    }
}

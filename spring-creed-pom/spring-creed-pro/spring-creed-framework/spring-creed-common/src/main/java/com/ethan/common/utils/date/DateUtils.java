/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.common.utils.date;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECONDS = "uuuu-MM-dd HH:mm:ssSSS";
    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "uuuu-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECONDS);

    /**
     * 秒转换成毫秒
     */
    public static final long SECOND_MILLIS = 1000;
    /**
     * 时区 - 默认
     */
    public static final String UTC_8 = "UTC+8";


    public static Date addTime(Duration duration) {
        return new Date(System.currentTimeMillis() + duration.toMillis());
    }

    public static boolean isExpired(Date time) {
        return System.currentTimeMillis() > time.getTime();
    }
    public static boolean isExpired(ZonedDateTime time) {
        return System.currentTimeMillis() > time.toInstant().toEpochMilli();
    }
    public static boolean isExpired(Instant time) {
        return System.currentTimeMillis() > time.toEpochMilli();
    }

    public static long diff(Date endTime, Date startTime) {
        return endTime.getTime() - startTime.getTime();
    }

    /**
     * 创建指定时间
     *
     * @param year        年
     * @param mouth       月
     * @param day         日
     * @return 指定时间
     */
    public static Date buildTime(int year, int mouth, int day) {
        return buildTime(year, mouth, day, 0, 0, 0);
    }

    /**
     * 创建指定时间
     *
     * @param year        年
     * @param mouth       月
     * @param day         日
     * @param hour        小时
     * @param minute      分钟
     * @param second      秒
     * @return 指定时间
     */
    public static Date buildTime(int year, int mouth, int day,
                                 int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, mouth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0); // 一般情况下，都是 0 毫秒
        return calendar.getTime();
    }

    public static Date max(Date a, Date b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) > 0 ? a : b;
    }

    public static boolean beforeNow(Date date) {
        return date.getTime() < System.currentTimeMillis();
    }

    public static boolean afterNow(Date date) {
        return date.getTime() >= System.currentTimeMillis();
    }

    /**
     * 计算当期时间相差的日期
     *
     * @param field  日历字段.<br/>eg:Calendar.MONTH,Calendar.DAY_OF_MONTH,<br/>Calendar.HOUR_OF_DAY等.
     * @param amount 相差的数值
     * @return 计算后的日志
     */
    public static Date addDate(int field, int amount) {
        return addDate(null, field, amount);
    }

    /**
     * 计算当期时间相差的日期
     *
     * @param date   设置时间
     * @param field  日历字段 例如说，{@link Calendar#DAY_OF_MONTH} 等
     * @param amount 相差的数值
     * @return 计算后的日志
     */
    public static Date addDate(Date date, int field, int amount) {
        if (amount == 0) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        if (date != null) {
            c.setTime(date);
        }
        c.add(field, amount);
        return c.getTime();
    }

    /**
     * 是否今天
     *
     * @param date 日期
     * @return 是否
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return LocalDate.now().equals(localDate);
    }
    @Deprecated(forRemoval = true)
    public static boolean isToday(ZonedDateTime date) {
        if (date == null) {
            return false;
        }
        LocalDate localDate = date.toLocalDate();
        return LocalDate.now().equals(localDate);
    }
    public static boolean isToday(Instant date) {
        if (date == null) {
            return false;
        }
        LocalDate localDate = LocalDate.ofInstant(date, ZoneId.of(UTC_8));
        return LocalDate.now().equals(localDate);
    }

    public static boolean expired(String timestamp, long timeoutSeconds) {
        if (StringUtils.isBlank(timestamp)) {
            return true;
        }
//        ZoneId.getAvailableZoneIds(), check all available zone ids
        LocalDateTime nowTime = ZonedDateTime.now(ZoneId.of(UTC_8)).toLocalDateTime();
        LocalDateTime expiringTime = LocalDateTime.parse(timestamp, DEFAULT_FORMATTER).plusSeconds(timeoutSeconds).atZone(ZoneId.of(UTC_8)).toLocalDateTime();
        return nowTime.isAfter(expiringTime);
    }


    public static Instant localDateTime2Instant(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.atZone(ZoneId.of(UTC_8)).toInstant();
    }
    public static LocalDateTime instant2LocalDateTime(Instant date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date, ZoneId.of(UTC_8));
    }

    public static Predicate instantCriteriaBuilder(CriteriaBuilder cb, @NotNull Expression<Instant> dateExpress, @NotNull LocalDateTime[] createTime) {
        if (ArrayUtils.getLength(createTime) > 1) {
            return cb.between(dateExpress, localDateTime2Instant(createTime[0]), localDateTime2Instant(createTime[1]));
        } else {
            return cb.greaterThanOrEqualTo(dateExpress, localDateTime2Instant(createTime[0]));
        }
    }
}

package com.ethan.common.converter;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashSet;
import java.util.Locale;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 11/10/24
 */
public class SetTypeConvertTest {
    @Test
    void setTypeConvertTest() {
        System.out.println(StringUtils.join(new HashSet<>(), ","));
    }


    @Test
    void testDateTimeFormat() {
        DateTimeFormatter formatter= new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MMM-uuuu").toFormatter(Locale.ENGLISH);

        var dtf = DateTimeFormatter.ofPattern("dd-MMM-uuuu");
        System.out.println(LocalDateTime.now().format(dtf));
        var dtf2 = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        System.out.println(LocalDateTime.now().format(dtf2));

        System.out.println(LocalDateTime.now().format(formatter));

        var piDate = LocalDate.parse("12-JUL-2024", formatter);
        var endDate = LocalDate.parse("12-AUG-2024", formatter);
        System.out.println(piDate.plusDays(10).isAfter(endDate));
        System.out.println(piDate.plusDays(30).isAfter(endDate));
        System.out.println(piDate.plusDays(31).isEqual(endDate));
        System.out.println(piDate.plusDays(32).isAfter(endDate));
    }
}

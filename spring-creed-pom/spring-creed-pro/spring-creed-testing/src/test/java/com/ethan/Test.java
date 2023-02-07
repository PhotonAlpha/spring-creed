package com.ethan;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Test {
    @org.junit.jupiter.api.Test
     void name() {
        System.out.println(new BigDecimal("1").compareTo(new BigDecimal(11)));
        System.out.println(new BigDecimal("12").compareTo(new BigDecimal(11)));
        System.out.println(new BigDecimal("11").compareTo(new BigDecimal(11)));
    }

    @org.junit.jupiter.api.Test
    void dateTime() {
        DateTimeFormatter DTF = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'.000.000'");
        // 2022-07-15T07:54:14.000.000
        System.out.println(ZonedDateTime.now().format(DTF));
    }
}

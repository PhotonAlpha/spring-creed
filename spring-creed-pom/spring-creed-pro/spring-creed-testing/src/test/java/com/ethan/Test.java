package com.ethan;

import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

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

    @org.junit.jupiter.api.Test
    void guavaTest() {
        Set<Integer> set1 = Sets.newHashSet(1, 2, 3, 4);
        Set<Integer> set2 = Sets.newHashSet(5, 3, 4, 7);

        List<Integer> intersection = Sets.intersection(set1, set2).stream().toList();
        System.out.println(intersection);
        List<Integer> diff = Sets.difference(set1, set2).stream().toList();
        System.out.println(diff);
        List<Integer> union = Sets.union(set1, set2).stream().toList();
        System.out.println(union);
    }
}

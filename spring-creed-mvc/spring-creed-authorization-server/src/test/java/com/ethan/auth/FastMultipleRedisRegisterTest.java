package com.ethan.auth;

import com.ethan.redis.multiple.FastMultipleRedisProperties;
import com.ethan.redis.multiple.FastRedisRegister;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class FastMultipleRedisRegisterTest {
  @Test
  public void testIntersection() {

    List<String> keys = Arrays.asList("include1", "include3", "include5");
    Set<String> include = new HashSet<>(Arrays.asList("include2", "Include3", "include6"));

    Collection<String> result = CollectionUtils.intersection(include, keys);
    System.out.println(result);
  }

  @Test
  void name() {
    String string = "12345";
    String test1 = String.format("%1$6s", string);
    System.out.println(test1);
    System.out.println(test1.substring(test1.length() - 6));
  }

  @Test
  void testDate() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMyyyy");

    LocalDate initial = LocalDate.of(2020, 1, 1);
    LocalDate start = initial.withDayOfMonth(1);
    if (initial.isEqual(start)) {
      System.out.println("first day");
      initial = start.minusDays(1L);
    } else {
      System.out.println("not first day");
    }

    String res = initial.format(dtf);
    System.out.println(res);

    //LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
    //System.out.println(start);
    //System.out.println(end);
  }
}

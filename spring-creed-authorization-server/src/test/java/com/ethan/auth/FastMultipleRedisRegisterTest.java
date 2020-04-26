package com.ethan.auth;

import com.ethan.redis.multiple.FastMultipleRedisProperties;
import com.ethan.redis.multiple.FastRedisRegister;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FastMultipleRedisRegisterTest {
  @Test
  public void testIntersection() {
    List<String> keys = Arrays.asList("include1", "include3", "include5");
    Set<String> include = new HashSet<>(Arrays.asList("include2", "Include3", "include6"));

    Collection<String> result = CollectionUtils.intersection(include, keys);
    System.out.println(result);
  }
}

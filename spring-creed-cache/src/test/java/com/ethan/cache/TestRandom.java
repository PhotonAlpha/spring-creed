package com.ethan.cache;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

public class TestRandom {
  @Test
  void name() {

    System.out.println(RandomStringUtils.randomAlphabetic(4) + System.nanoTime());
  }
}

package com.ethan.gradation.util;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class MathUtils {
  private MathUtils() {
  }
  public static long calculateInterval(@NotNull TimeUnit timeUnit, long expire) {
    long timeSeconds = timeUnit.toSeconds(expire);
    int intervalValue = Math.round(timeSeconds / 10);
    int intervalValueRandom = ThreadLocalRandom.current().nextInt(intervalValue);
    return timeSeconds - intervalValueRandom;
  }
}

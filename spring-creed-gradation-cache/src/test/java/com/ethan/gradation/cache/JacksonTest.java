package com.ethan.gradation.cache;

import com.ethan.context.utils.InstanceUtils;
import com.ethan.gradation.config.CaffeineCacheProperty;
import com.ethan.gradation.config.RedisCacheProperty;
import com.ethan.gradation.util.MathUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class JacksonTest {
  @Test
  void name() throws IOException {
    TimeUnit timeUnit = TimeUnit.MINUTES;
    Long expire = 100L;
    long timeSeconds = timeUnit.toSeconds(expire);
    int intervalValue = Math.round(timeSeconds / 10);
    int intervalValueRandom = ThreadLocalRandom.current().nextInt(intervalValue);
    long result = MathUtils.calculateInterval(timeUnit, expire);
    System.out.println("----timeSeconds----" + timeSeconds);
    System.out.println("----intervalValue----" + intervalValue);
    System.out.println("----intervalValueRandom----" + intervalValueRandom);
    System.out.println("----result----" + result);
    //ObjectMapper mapper = InstanceUtils.getMapperInstance();
    //CaffeineCacheProperty p = new CaffeineCacheProperty();
    //p.setExpireTime(1);
    //p.setStats(true);
    //String res = mapper.writeValueAsString(p);
    //System.out.println(res);
    //
    //ObjectMapper mapper2 = new ObjectMapper();
    //RedisCacheProperty p2 = new RedisCacheProperty();
    //p2.setAllowNullValue(false);
    //JsonNode node = mapper2.valueToTree(p2);
    //System.out.println(node.toString());
    for (int i = 0; i < 10; i++) {
      int res = ThreadLocalRandom.current().nextInt(10);

      System.out.println(res);
    }
  }

}

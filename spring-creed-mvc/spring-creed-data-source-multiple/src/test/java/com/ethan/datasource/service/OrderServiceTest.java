package com.ethan.datasource.service;

import com.ethan.datasource.DataSourceMultipleApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DataSourceMultipleApplication.class)
@TestPropertySource(locations = "classpath:application.yml")
public class OrderServiceTest {
  @Autowired
  private OrderService orderService;

  @Test
  public void testMethod01() {
    orderService.method01();
  }

  @Test
  public void testMethod02() {
    orderService.method02();
  }

  @Test
  public void testMethod03() {
    orderService.method03();
  }

  @Test
  public void testMethod04() {
    orderService.method04();
  }

  @Test
  public void testMethod05() {
    orderService.method05();
  }
}

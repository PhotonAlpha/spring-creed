package com.ethan.datasource.dao;

import com.ethan.datasource.dao.order.OrderDao;
import com.ethan.datasource.model.order.OrderDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderDaoTest {
  @Autowired
  private OrderDao orderRepository;

  @Test
  public void testSelectById() {
    OrderDO order = orderRepository.findById(1L)
        .orElse(null); // 为空，则返回 null
    System.out.println(order);
  }
}

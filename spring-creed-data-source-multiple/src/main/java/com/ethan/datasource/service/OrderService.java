package com.ethan.datasource.service;

import com.ethan.datasource.constant.DBConstants;
import com.ethan.datasource.dao.order.OrderDao;
import com.ethan.datasource.dao.user.UserDao;
import com.ethan.datasource.model.order.OrderDO;
import com.ethan.datasource.model.user.UserDO;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class OrderService {
  @Autowired
  private OrderDao orderRepository;
  @Autowired
  private UserDao userRepository;

  @PostConstruct
  public void init() {
    UserDO userDo = new UserDO();
    userDo.setId(1L);
    userDo.setUsername("xiaoming");
    userDo.setPassword("xiaoming");
    userDo.setCreateTime(new Date());
    userRepository.save(userDo);

    OrderDO order = new OrderDO();
    order.setId(1L);
    order.setUserId(1L);
    orderRepository.save(order);
  }

  private OrderService self() {
    return (OrderService) AopContext.currentProxy();
  }

  public void method01() {
    // 查询订单
    OrderDO order = orderRepository.findById(1L).orElse(null);
    System.out.println("method01-----");
    System.out.println(order);
    // 查询用户
    UserDO user = userRepository.findById(1L).orElse(null);
    System.out.println(user);
  }

  @Transactional
  public void method02() {
    // 查询订单
    OrderDO order = orderRepository.findById(1L).orElse(null);
    System.out.println("method02-----");
    System.out.println(order);
    // 查询用户
    UserDO user = userRepository.findById(1L).orElse(null);
    System.out.println(user);
  }

  public void method03() {
    // 查询订单
    self().method031();
    // 查询用户
    self().method032();
  }

  @Transactional
  public void method031() {
    OrderDO order = orderRepository.findById(1L).orElse(null);
    System.out.println("method031-----");
    System.out.println(order);
  }

  @Transactional
  public void method032() {
    UserDO user = userRepository.findById(1L).orElse(null);
    System.out.println("method032-----");
    System.out.println(user);
  }

  public void method04() {
    // 查询订单
    self().method041();
    // 查询用户
    self().method042();
  }

  @Transactional
  public void method041() {
    OrderDO order = orderRepository.findById(1L).orElse(null);
    System.out.println("method041-----");
    System.out.println(order);
  }

  @Transactional
  public void method042() {
    UserDO user = userRepository.findById(1L).orElse(null);
    System.out.println("method042-----");
    System.out.println(user);
  }

  @Transactional
  public void method05() {
    // 查询订单
    OrderDO order = orderRepository.findById(1L).orElse(null);
    System.out.println("method05-----");
    System.out.println(order);
    // 查询用户
    self().method052();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void method052() {
    System.out.println("method052-----");
    UserDO user = userRepository.findById(1L).orElse(null);
    System.out.println(user);
  }
}

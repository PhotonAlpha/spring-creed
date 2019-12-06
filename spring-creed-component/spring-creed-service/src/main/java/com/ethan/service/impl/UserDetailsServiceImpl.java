package com.ethan.service.impl;

import com.ethan.model.Producer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    Producer producer = new Producer();
    producer.setUsername("xiaoming");
    producer.setPassword("123");
    return producer;
  }
}

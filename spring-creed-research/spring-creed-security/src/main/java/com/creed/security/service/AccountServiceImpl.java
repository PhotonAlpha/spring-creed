package com.creed.service;

import com.creed.model.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

  private List<Account> accounts = new ArrayList<>();

 @PostConstruct
 public void onInit() {
   accounts.add(
    Account.builder().id(1).email("a@email.com").username("user1").password("123").roleString("role_normal").build()
   );
   accounts.add(
    Account.builder().id(2).email("a@email.com").username("user2").password("123").roleString("role_normal").build()
   );
   accounts.add(
    Account.builder().id(3).email("a@email.com").username("user3").password("123").roleString("role_normal").build()
   );
   accounts.add(
    Account.builder().id(4).email("a@email.com").username("user4").password("123").roleString("role_normal").build()
   );
 }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Assert.notNull(username, "username can not be null");
    Assert.notNull(username, "password can not be null");
    return accounts.stream()
        .filter(acc -> username.equalsIgnoreCase(acc.getUsername()))
        .findFirst().orElse(null);
  }
}

package com.creed.service;

import com.creed.model.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
  Account addAccount(Account account);
}

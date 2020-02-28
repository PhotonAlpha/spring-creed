package com.creed;

import lombok.Data;

@Data
public class Account {
  private Integer id;
  private String userName;
  private String email;
  private String password;
  private String roleString;
}

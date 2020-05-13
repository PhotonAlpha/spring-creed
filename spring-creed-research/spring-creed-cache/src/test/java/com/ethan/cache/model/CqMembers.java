package com.ethan.cache.model;

import lombok.Data;

import java.util.Date;

@Data
public class CqMembers {
  private Long id;
  private String ip;
  private String name;
  private String gender;
  private Integer age;
  private String password;
  private String email;
  private byte[] profileIcon;
  private String level;
  private String authority;
  private Date registrationTime;
  private Date birthday;
  private String mobile;
  private String nickname;
}

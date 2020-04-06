package com.ethan.vo;

import com.ethan.entity.BaseDo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BloggerVO extends BaseDo {
  private Long bloggerId;

  private String name;

  private String phone;

  private String email;

  private String password;

  private String nickName;

  private Date registrationTime;

  private String birth;

  private Short age;

  private Character gender;

  private String ip;

  private byte[] avatar;

  private String level;

  private Date lastPasswordUpdate;

  private Boolean accountNonLocked;

  private Boolean accountNonExpired;

  private List<RoleVO> roles;

  private List<GroupVO> groups;

}

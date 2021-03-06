package com.ethan.app.dto;

import com.ethan.entity.BaseDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BloggerDTO extends BaseDO {
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

  private List<RoleDTO> roles;

  private List<GroupDTO> groups;

}

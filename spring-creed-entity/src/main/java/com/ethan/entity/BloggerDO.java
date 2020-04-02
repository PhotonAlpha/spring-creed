package com.ethan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "ethan_bloggers")
public class BloggerDO extends BaseDo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "b_id", length = 20)
  private Long bloggerId;

  @Column(name = "b_name", length = 50)
  private String name;

  @Column(name = "b_phone", length = 20)
  @Max(value = 20)
  private String phone;

  @Column(name = "b_email")
  private String email;

  @Column(name = "b_pwd")
  private String password;

  @Column(name = "b_nick_name", length = 50)
  private String nickName;

  @Column(name = "b_registration_time")
  private Date registrationTime;

  @Column(name = "b_birth")
  private String birth;

  @Column(name = "b_age")
  private Short age;

  @Column(name = "b_gender")
  private Character gender;

  @Column(name = "b_ip")
  private String ip;

  @Column(name = "b_avatar")
  private byte[] avatar;

  @Column(name = "b_level")
  private String level;

  @Column(name = "b_last_password_update")
  private Date lastPasswordUpdate;

  @Column(name = "b_account_non_locked")
  private Boolean accountNonLocked;

  @Column(name = "b_account_non_expired")
  private Boolean accountNonExpired;


  /**
   * https://stackoverflow.com/questions/15359306/how-to-load-lazy-fetched-items-from-hibernate-jpa-in-my-controller
   */
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "bloggers")
  @JoinTable(name = "ethan_blogger_role",
      joinColumns = {@JoinColumn(name = "b_id", referencedColumnName = "b_id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "role_id")})
  private List<RoleDO> roles;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "bloggers")
  @JoinTable(name = "ethan_blogger_group",
      joinColumns = {@JoinColumn(name = "b_id", referencedColumnName = "b_id")},
      inverseJoinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "group_id")})
  private List<GroupDO> groups;
}

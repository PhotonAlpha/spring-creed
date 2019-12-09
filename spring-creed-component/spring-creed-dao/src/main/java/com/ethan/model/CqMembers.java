package com.ethan.model;

import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "cq_members")
@Data
public class CqMembers {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "m_id", length = 20)
  private Long id;
  @Column(name = "m_ip", length = 20)
  private String ip;
  @Column(name = "m_name", length = 20)
  private String name;
  @Column(name = "m_gender", length = 2)
  private String gender;
  @Column(name = "m_age")
  private Integer age;
  @Column(name = "m_password", length = 20)
  private String password;
  @Column(name = "m_email", length = 20)
  private String email;

  @Lob
  @Basic(fetch = FetchType. LAZY)
  @Column(name = "m_profile_icon", columnDefinition = "BLOB")
  private byte[] profileIcon;
  @Column(name = "m_level", length = 20)
  private String level;
  @Column(name = "m_authority", length = 20)
  private String authority;
  @Column(name = "m_registration_time")
  private Date registrationTime;
  @Column(name = "m_birthday")
  private Date birthday;
  @Column(name = "m_mobile", length = 13)
  private String mobile;
  @Column(name = "m_nickname", length = 13)
  private String nickname;
}

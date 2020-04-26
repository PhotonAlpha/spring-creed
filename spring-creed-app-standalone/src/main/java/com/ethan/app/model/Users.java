package com.ethan.app.model;

import lombok.Getter;
import lombok.Setter;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "USERNAME", length = 50, unique = true)
  @NotNull
  @Size(min = 4, max = 50)
  private String username;

  @Column(name = "PASSWORD", length = 100)
  @NotNull
  @Size(min = 4, max = 100)
  private String password;

  @Column(name = "FIRSTNAME", length = 50)
  @NotNull
  @Size(min = 4, max = 50)
  private String firstname;

  @Column(name = "LASTNAME", length = 50)
  @NotNull
  @Size(min = 4, max = 50)
  private String lastname;

  @Column(name = "EMAIL", length = 50)
  @NotNull
  @Size(min = 4, max = 50)
  private String email;

  @Column(name = "ENABLED")
  @NotNull
  private Boolean enabled;

  @Column(name = "LASTPASSWORDRESETDATE")
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date lastPasswordResetDate;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "USER_AUTHORITYS",
      joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
      inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
  private List<Authoritys> authorities;

}

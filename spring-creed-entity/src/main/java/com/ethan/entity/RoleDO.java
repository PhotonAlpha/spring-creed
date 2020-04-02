package com.ethan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Table(name = "ethan_role")
public class RoleDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_id", length = 20)
  private Long roleId;

  @NotNull
  @Column(name = "role_name", length = 20)
  @Enumerated(EnumType.STRING)
  private AuthorityEnum roleName;

  @ManyToMany(fetch = FetchType.LAZY)
  private List<BloggerDO> bloggers;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "ethan_role_group",
    joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
    inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id")
  )
  private List<GroupDO> groups;

}

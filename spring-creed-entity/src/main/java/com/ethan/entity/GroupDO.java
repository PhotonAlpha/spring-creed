package com.ethan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "ethan_group")
public class GroupDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id", length = 20)
  private Long groupId;

  @Column(name = "group_parent_id", length = 20)
  private Long groupParentId;

  @Column(name = "group_name", length = 20)
  private String groupName;

  @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
  private List<BloggerDO> bloggers;

  @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
  private List<RoleDO> roles;
}

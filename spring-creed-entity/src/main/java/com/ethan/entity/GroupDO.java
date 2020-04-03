package com.ethan.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
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
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "ethan_group")
public class GroupDO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id", length = 20)
  private Long groupId;

  @Column(name = "group_parent_id", length = 20)
  private Long groupParentId = 0L;

  @Column(name = "group_name", length = 20)
  @Enumerated(EnumType.STRING)
  private GroupEnum groupName;

  @ManyToMany(mappedBy = "groups")
  private List<BloggerDO> bloggers;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "ethan_role_group",
      joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
  )
  private List<RoleDO> roles;
}

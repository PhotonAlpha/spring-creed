package com.ethan.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * 用户组表，用来组成权限的一部分
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
// @NoArgsConstructor(access = AccessLevel.PACKAGE)
// @AllArgsConstructor(access = AccessLevel.PACKAGE)
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
  @Enumerated(EnumType.STRING)
  private GroupEnum groupName;

  @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
  private List<BloggerDO> bloggers;

  @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
  private List<RoleDO> roles;

  @Override
  public String toString() {
    return "GroupDO{" +
        "groupId=" + groupId +
        ", groupParentId=" + groupParentId +
        ", groupName=" + groupName +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (! (o instanceof GroupDO)) return false;

    GroupDO groupDO = (GroupDO) o;

    return new EqualsBuilder()
        .append(groupId, groupDO.groupId)
        .append(groupName, groupDO.groupName)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(groupId)
        .append(groupName)
        .toHashCode();
  }
}

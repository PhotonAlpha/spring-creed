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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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
  @JoinTable(name = "ethan_role_group",
      joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id")
  )
  private List<GroupDO> groups;

  @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
  private List<BloggerDO> bloggers;


  @Override
  public String toString() {
    return "RoleDO{" +
        "roleId=" + roleId +
        ", roleName=" + roleName +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (! (o instanceof RoleDO)) return false;

    RoleDO roleDO = (RoleDO) o;

    return new EqualsBuilder()
        .append(roleId, roleDO.roleId)
        .append(roleName, roleDO.roleName)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(roleId)
        .append(roleName)
        .toHashCode();
  }
}

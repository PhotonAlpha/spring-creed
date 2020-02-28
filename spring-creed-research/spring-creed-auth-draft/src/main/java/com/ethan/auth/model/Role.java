package auth.model;

import com.ethan.auth.domain.Base;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "um_t_role")
public class Role extends Base implements Serializable {
  private static final long serialVersionUID = 7244616863672882746L;
  /**
   * 角色名
   */
  @Column(name = "name")
  private String name;

  /**
   * 角色
   */
  @Column(name = "role")
  private String role;

  /**
   * 角色 -- 用户: 1对多
   */
  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "um_t_role_user", joinColumns = {@JoinColumn(name = "roleId")}, inverseJoinColumns = {@JoinColumn(name = "userId")})
  private Set<User> users;
}

package com.ethan.system.dal.entity.permission;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.constant.SexEnum;
import com.ethan.common.pojo.BaseVersioningXDO;
import com.ethan.system.dal.entity.dept.SystemDeptUsers;
import com.ethan.system.dal.entity.dept.SystemPostUsers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "creed_system_users", indexes = {
        @Index(name = "CSU_IDX_COMMON", columnList = "username,email,phone")
})
@NamedEntityGraphs({
        @NamedEntityGraph(name = "User.list", attributeNodes = {
            @NamedAttributeNode("deptUsers"),
            @NamedAttributeNode("postUsers")
        }),
        @NamedEntityGraph(name = "User.details", attributeNodes = {
                @NamedAttributeNode("groupUsers"),
                @NamedAttributeNode("userRoles"),
                @NamedAttributeNode("userAuthorities")
        })
})
@DynamicUpdate
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"groupUsers", "userRoles", "userAuthorities", "deptUsers", "postUsers"})
@Accessors(chain = true)
public class SystemUsers extends BaseVersioningXDO implements UserDetails {
    @Id
    // @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.security.utils.SnowFlakeIdGenerator")
    // @GeneratedValue(generator = "snowflakeId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * 用户账号
     */
    @Column(name = "username", nullable = false)
    private String username;
    /**
     * 加密后的密码
     *
     * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
     */
    @Column(name = "password", nullable = false)
    private String password;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 备注
     */
    private String remark;


    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 手机号码
     */
    private String phone;
    private String countryCode;
    /**
     * 用户性别
     * <p>
     * 枚举类 {@link SexEnum}
     * {@see https://blog.csdn.net/wanping321/article/details/90269057}
     */
    @Column
    @Convert(converter = SexEnum.Converter.class)
    private SexEnum sex;
    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private Instant loginDate;

    @Column
    @Convert(converter = CommonStatusEnum.Converter.class)
    private CommonStatusEnum accNonExpired = CommonStatusEnum.ENABLE;
    @Column
    @Convert(converter = CommonStatusEnum.Converter.class)
    private CommonStatusEnum accNonLocked = CommonStatusEnum.ENABLE;
    @Column
    @Convert(converter = CommonStatusEnum.Converter.class)
    private CommonStatusEnum credentialsNonExpired = CommonStatusEnum.ENABLE;


    /**
     * Cascading means that if you insert, update or delete an object, related objects are inserted, updated or deleted as well.
     * <p>
     * https://stackoverflow.com/questions/40939621/many-to-many-save-update-in-joining-table-and-one-table-only
     */
    // @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = CreedAuthorities.class)
    // 此处不能使用 cascade
    // @ManyToMany(targetEntity = CreedAuthorities.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    // @JoinTable(name = "creed_user_authorities",
    //         // joinColumns,当前对象在中间表中的外键
    //         joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    //         // inverseJoinColumns，对方对象在中间表的外键
    //         inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    // @JsonIgnore
    // private Set<CreedAuthorities> authorities = Collections.emptySet();

/*     @OneToMany(targetEntity = CreedConsumerAuthorities.class, mappedBy = "consumer",
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CreedConsumerAuthorities> consumerAuthorities = new ArrayList<>(); */

    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<SystemGroupUsers> groupUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<SystemUserRoles> userRoles;

    @JsonIgnore
    @OneToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<SystemUserAuthorities> userAuthorities;

    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<SystemDeptUsers> deptUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<SystemPostUsers> postUsers;

    /**
     * 部门 ID
     */
    @Transient
    private Long deptId;
    /**
     * 岗位编号数组
     */
    @Transient
    private Set<Long> postIds;

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //1.1 获取用户组
        //1.2 获取用户组权限
        // Set<String> groupAuthoritiesList = Optional.ofNullable(groupUsers).orElse(Collections.emptyList())
        //         .stream().map(SystemGroupUsers::getGroups)
        //         .map(SystemGroups::getGroupRoles)
        //         .flatMap(Collection::stream)
        //         .map(SystemGroupRoles::getRoles)
        //         .map(SystemRoles::getRoleAuthorities)
        //         .flatMap(Collection::stream)
        //         .map(SystemRoleAuthorities::getAuthorities)
        //         .map(SystemAuthorities::getAuthority)
        //         .collect(Collectors.toSet());

        //2.1 获取用户角色
        //2.2 获取用户角色权限
        // Set<String> roleAuthoritiesList = Optional.ofNullable(userRoles).orElse(Collections.emptyList())
        //         .stream().map(SystemUserRoles::getRoles)
        //         .map(SystemRoles::getRoleAuthorities)
        //         .flatMap(Collection::stream)
        //         .map(SystemRoleAuthorities::getAuthorities)
        //         .map(SystemAuthorities::getAuthority)
        //         .collect(Collectors.toSet());
        //3 获取用户角色权限
        Set<String> userAuthoritiesList = Optional.ofNullable(userAuthorities).orElse(Collections.emptyList())
                .stream().map(SystemUserAuthorities::getAuthorities)
                .map(SystemAuthorities::getAuthority)
                .collect(Collectors.toSet());
        var authorities = new HashSet<>(userAuthoritiesList);
        // authorities.addAll(roleAuthoritiesList);
        // authorities.addAll(groupAuthoritiesList);
        return authorities.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return CommonStatusEnum.ENABLE.equals(accNonExpired);
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return CommonStatusEnum.ENABLE.equals(accNonLocked);
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return CommonStatusEnum.ENABLE.equals(credentialsNonExpired);
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return CommonStatusEnum.ENABLE.equals(enabled);
    }
}

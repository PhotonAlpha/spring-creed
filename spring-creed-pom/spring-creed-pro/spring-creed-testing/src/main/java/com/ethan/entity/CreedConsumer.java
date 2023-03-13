package com.ethan.entity;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.constant.SexEnum;
import com.ethan.listener.CreedConsumerEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "creed_consumer")
@Data
@EqualsAndHashCode
@ToString(exclude = "consumerAuthorities")
public class CreedConsumer extends BaseDO {
    @Id
    @Column
    @GenericGenerator(name = "snowflakeId", strategy = "com.ethan.utils.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowflakeId")
    protected String id;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 加密后的密码
     *
     * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
     */
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
    private String phoneCode;
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
/*     @ManyToMany(targetEntity = CreedAuthorities.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "creed_consumer_authorities",
            //joinColumns,当前对象在中间表中的外键
            joinColumns = @JoinColumn(name = "consumer_id", referencedColumnName = "id"),
            //inverseJoinColumns，对方对象在中间表的外键
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private Set<CreedAuthorities> authorities; */

    @OneToMany(targetEntity = CreedConsumerAuthorities.class, mappedBy = "consumer",
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CreedConsumerAuthorities> consumerAuthorities = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CreedConsumer that = (CreedConsumer) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }


    // 返回类型定义
    @DomainEvents
    public List<Object> domainEvents(){
        System.out.println("CreedConsumerEvent domainEvents");
        return Stream.of(new CreedConsumerEvent(this)).collect(Collectors.toList());
    }
    // 事件发布后callback
    @AfterDomainEventPublication
    void callback() {
        System.err.println("CreedConsumerEvent ok");
    }
}

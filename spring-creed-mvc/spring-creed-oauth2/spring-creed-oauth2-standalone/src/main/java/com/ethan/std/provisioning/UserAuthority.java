package com.ethan.std.provisioning;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/2/2022 5:21 PM
 */
@Data
@Entity
@Table(name = "e_authorities")
public class UserAuthority implements GrantedAuthority, Serializable {
    private static final long serialVersionUID = 6409868875644708960L;
    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "authority")
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public String toString() {
        return "UserAuthority{" +
                "username='" + username + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}

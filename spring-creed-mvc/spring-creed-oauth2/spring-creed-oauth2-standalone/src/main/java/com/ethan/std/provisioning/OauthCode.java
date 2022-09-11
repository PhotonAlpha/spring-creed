package com.ethan.std.provisioning;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 10:10 AM
 */
@Data
@Entity
@Table(name = "oauth_code")
public class OauthCode implements Serializable {
    @Column(name = "code")
    private String code;

    @Id
    @Column(name = "authentication")
    private String authentication;

    @Override
    public String toString() {
        return "OauthCode{" +
                "code='" + code + '\'' +
                ", authentication='" + authentication + '\'' +
                '}';
    }
}

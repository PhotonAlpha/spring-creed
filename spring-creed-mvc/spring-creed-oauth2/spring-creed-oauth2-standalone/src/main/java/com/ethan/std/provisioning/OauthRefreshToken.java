package com.ethan.std.provisioning;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 10:09 AM
 */
@Data
@Entity
@Table(name = "oauth_refresh_token")
public class OauthRefreshToken implements Serializable {
    @Id
    @Column(name = "token_id")
    private String tokenId;

    @Lob
    @Column(name = "token")
    private byte[] token;

    @Lob
    @Column(name = "authentication")
    private byte[] authentication;

    @Column(name = "create_time")
    private LocalDateTime createTime;


    public OauthRefreshToken() {
    }

    public OauthRefreshToken(String tokenId, byte[] token) {
        this.tokenId = tokenId;
        this.token = token;
    }

    public OauthRefreshToken(String tokenId, byte[] token, byte[] authentication) {
        this.tokenId = tokenId;
        this.token = token;
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        return "OauthRefreshToken{" +
                "tokenId='" + tokenId + '\'' +
                '}';
    }
}

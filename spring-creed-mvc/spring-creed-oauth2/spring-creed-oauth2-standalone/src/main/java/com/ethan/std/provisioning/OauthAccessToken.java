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
 * @date: 8/4/2022 10:07 AM
 */
@Data
@Entity
@Table(name = "oauth_access_token")
public class OauthAccessToken implements Serializable {

    private static final long serialVersionUID = 1695644124811185139L;

    public OauthAccessToken() {
    }

    public OauthAccessToken(String tokenId, byte[] token) {
        this.tokenId = tokenId;
        this.token = token;
    }

    public OauthAccessToken(String tokenId, String authenticationId, byte[] authentication) {
        this.tokenId = tokenId;
        this.authenticationId = authenticationId;
        this.authentication = authentication;
    }

    @Id
    @Column(name = "token_id")
    private String tokenId;

    @Lob
    @Column(name = "token")
    private byte[] token;


    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_id")
    private String clientId;

    @Lob
    @Column(name = "authentication")
    private byte[] authentication;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Override
    public String toString() {
        return "OauthAccessToken{" +
                "tokenId='" + tokenId + '\'' +
                ", authenticationId='" + authenticationId + '\'' +
                ", userName='" + userName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}

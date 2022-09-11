package com.ethan.std.provisioning;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/2/2022 5:12 PM
 */

@Data
@Entity
@Table(name = "oauth_client_token")
public class Oauth2ClientToken implements Serializable {

    private static final long serialVersionUID = 4604838613938553455L;

    public Oauth2ClientToken() {
    }

    public Oauth2ClientToken(String tokenId, byte[] token) {
        this.tokenId = tokenId;
        this.token = token;
    }

    @Column(name = "token_id")
    private String tokenId;

    @Lob
    @Column(name = "token")
    private byte[] token;

    @Id
    @Column(name = "authentication_id")
    private String authenticationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "client_id")
    private String clientId;

    @Override
    public String toString() {
        return "Oauth2ClientToken{" +
                "tokenId='" + tokenId + '\'' +
                ", authenticationId='" + authenticationId + '\'' +
                ", userName='" + userName + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}

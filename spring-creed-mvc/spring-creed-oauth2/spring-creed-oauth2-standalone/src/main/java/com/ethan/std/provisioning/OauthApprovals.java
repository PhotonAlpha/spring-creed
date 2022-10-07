package com.ethan.std.provisioning;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 3:04 PM
 */
@Data
@Entity
@Table(name = "oauth_approvals")
public class OauthApprovals implements Serializable {
    private static final long serialVersionUID = -212219150717647187L;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "scope")
    private String scope;

    @Column(name = "status")
    private String status;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @Column(name = "last_modified_at")
    private ZonedDateTime lastModifiedAt;

    @Override
    public String toString() {
        return "OauthApprovals{" +
                "userId='" + userId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", scope='" + scope + '\'' +
                ", status='" + status + '\'' +
                ", expiresAt=" + expiresAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}

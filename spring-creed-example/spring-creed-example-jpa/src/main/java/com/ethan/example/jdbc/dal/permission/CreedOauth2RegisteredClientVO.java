package com.ethan.example.jdbc.dal.permission;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/3/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "creed_oauth2_registered_client")
public class CreedOauth2RegisteredClientVO {
    // private String id;
    private String clientId;

    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "registered_client_id")
    private List<CreedOAuth2AuthorizationVO> auth2Authorizations;
}

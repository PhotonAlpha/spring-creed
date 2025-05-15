package com.ethan.example.jpa.dal.oauth2;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/3/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Immutable
@ToString(exclude = "auth2Authorizations")
@Entity(name = "creed_oauth2_registered_client")
public class CreedOauth2RegisteredClientVO {
    @Id
    private String id;
    private String clientId;

    @OneToMany(mappedBy = "registeredClients")
    private List<CreedOAuth2AuthorizationVO> auth2Authorizations;
}

package com.ethan.system.dal.entity.oauth2.graph;

import com.google.common.base.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/3/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "auth2Authorizations")
@Entity(name = "creed_oauth2_registered_client")
public class CreedOauth2RegisteredClientVO {
    @Id
    private String id;
    private String clientId;

    @OneToMany(mappedBy = "registeredClients")
    private List<CreedOAuth2AuthorizationVO> auth2Authorizations;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreedOauth2RegisteredClientVO that = (CreedOauth2RegisteredClientVO) o;
        return Objects.equal(id, that.id) && Objects.equal(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, clientId);
    }
}

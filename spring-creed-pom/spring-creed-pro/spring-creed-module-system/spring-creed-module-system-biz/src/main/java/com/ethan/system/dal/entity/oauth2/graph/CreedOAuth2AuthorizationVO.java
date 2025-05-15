package com.ethan.system.dal.entity.oauth2.graph;

import com.google.common.base.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "registeredClients")
@Entity(name = "creed_oauth2_authorization")
@NamedEntityGraph(
        name = "authorization-all",
        attributeNodes = {
                @NamedAttributeNode("registeredClients")
        }
)
public class CreedOAuth2AuthorizationVO {
    @Id
    private String id;
    // private String registeredClientId;
    private String principalName;
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;

    @ManyToOne
    @JoinColumn(name = "registered_client_id", referencedColumnName = "id")
    private CreedOauth2RegisteredClientVO registeredClients;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreedOAuth2AuthorizationVO that = (CreedOAuth2AuthorizationVO) o;
        return Objects.equal(id, that.id) && Objects.equal(principalName, that.principalName) && Objects.equal(accessTokenValue, that.accessTokenValue) && Objects.equal(accessTokenIssuedAt, that.accessTokenIssuedAt) && Objects.equal(refreshTokenValue, that.refreshTokenValue) && Objects.equal(refreshTokenIssuedAt, that.refreshTokenIssuedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, principalName, accessTokenValue, accessTokenIssuedAt, refreshTokenValue, refreshTokenIssuedAt);
    }
}

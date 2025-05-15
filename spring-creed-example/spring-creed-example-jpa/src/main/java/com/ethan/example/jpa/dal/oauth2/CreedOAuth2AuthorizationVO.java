package com.ethan.example.jpa.dal.oauth2;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
// import org.springframework.data.annotation.Immutable;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Immutable
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    // private String registeredClientId;
    private String principalName;
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    // @Transient
    // private String clientId;
    // @OneToMany(mappedBy = "auth2Authorizations")
    @ManyToOne
    @JoinColumn(name = "registered_client_id", referencedColumnName = "id")
    private CreedOauth2RegisteredClientVO registeredClients;

}

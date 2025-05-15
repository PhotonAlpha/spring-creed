package com.ethan.system.dal.entity.oauth2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "creed_oauth2_authorization_consent")
@IdClass(CreedOAuth2AuthorizationConsent.AuthorizationConsentId.class)
@Data
@EqualsAndHashCode
public class CreedOAuth2AuthorizationConsent {
    @Id
    private String registeredClientId;
    @Id
    private String principalName;

    @Column(length = 1000)
    private String authorities;

    public static class AuthorizationConsentId implements Serializable {
        private String registeredClientId;
        private String principalName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuthorizationConsentId that = (AuthorizationConsentId) o;
            return registeredClientId.equals(that.registeredClientId) && principalName.equals(that.principalName);
        }
        @Override
        public int hashCode() {
            return Objects.hash(registeredClientId, principalName);
        }
    }
}

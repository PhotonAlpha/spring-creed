package com.ethan.std;

import lombok.Data;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Collection;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/5/2022 4:59 PM
 */
@Data
public class CustAuth extends AbstractAuthenticationToken {
    private OAuth2Request storedRequest;



    public CustAuth(Collection<? extends GrantedAuthority> authorities, OAuth2Request storedRequest) {
        super(authorities);
        this.storedRequest = storedRequest;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}

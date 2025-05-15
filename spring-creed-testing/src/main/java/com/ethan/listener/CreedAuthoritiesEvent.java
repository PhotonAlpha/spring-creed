/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.listener;

import com.ethan.security.websecurity.entity.CreedAuthorities;

public class CreedAuthoritiesEvent {
    private CreedAuthorities authorities;

    public CreedAuthoritiesEvent(CreedAuthorities authorities) {
        this.authorities = authorities;
    }
}

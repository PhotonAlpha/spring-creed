/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.listener;

import com.ethan.entity.CreedConsumerAuthorities;

public class CreedConsumerAuthoritiesEvent {
    private CreedConsumerAuthorities creedConsumerAuthorities;

    public CreedConsumerAuthoritiesEvent(CreedConsumerAuthorities creedConsumerAuthorities) {
        this.creedConsumerAuthorities = creedConsumerAuthorities;
    }
}

/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.listener;

import com.ethan.security.websecurity.entity.CreedUser;

public class CreedConsumerEvent {
    private CreedUser user;

    public CreedConsumerEvent(CreedUser user) {
        this.user = user;
    }
}

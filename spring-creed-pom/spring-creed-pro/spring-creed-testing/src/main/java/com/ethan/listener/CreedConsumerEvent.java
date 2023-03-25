/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.listener;

import com.ethan.entity.CreedConsumer;

public class CreedConsumerEvent {
    private CreedConsumer consumer;

    public CreedConsumerEvent(CreedConsumer consumer) {
        this.consumer = consumer;
    }
}

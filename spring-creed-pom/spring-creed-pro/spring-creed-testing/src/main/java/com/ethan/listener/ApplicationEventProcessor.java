/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ApplicationEventProcessor {
    private static final Logger log = LoggerFactory.getLogger(ApplicationEventProcessor.class);
    // @EventListener
    // public void departmentCreated(DepartmentEvent departmentEvent) {
    //     log.info("dept-event1:" + departmentEvent);
    // }
    // @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saleOrderCreated(CreedConsumerEvent saleOrderEvent) {
        log.info("CreedConsumerEvent succeed1:" + saleOrderEvent);
    }
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saleOrderCreatedBefore(CreedConsumerEvent saleOrderEvent) {
        log.info("CreedConsumerEvent succeed2:" + saleOrderEvent);
    }
    // @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void saleOrderCreatedFailed(CreedConsumerEvent saleOrderEvent) {
        log.info("CreedConsumerEvent:" + saleOrderEvent);
    }

    // @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saleOrderCreated(CreedAuthoritiesEvent saleOrderEvent) {
        log.info("CreedAuthoritiesEvent succeed1:" + saleOrderEvent);
    }
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saleOrderCreatedBefore(CreedAuthoritiesEvent saleOrderEvent) {
        log.info("CreedAuthoritiesEvent succeed2:" + saleOrderEvent);
    }
    // @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void saleOrderCreatedFailed(CreedAuthoritiesEvent saleOrderEvent) {
        log.info("CreedAuthoritiesEvent:" + saleOrderEvent);
    }

    // @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void saleOrderCreated(CreedConsumerAuthoritiesEvent saleOrderEvent) {
        log.info("CreedConsumerAuthoritiesEvent succeed1:" + saleOrderEvent);
    }
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saleOrderCreatedBefore(CreedConsumerAuthoritiesEvent saleOrderEvent) {
        log.info("CreedConsumerAuthoritiesEvent succeed2:" + saleOrderEvent);
    }
    // @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void saleOrderCreatedFailed(CreedConsumerAuthoritiesEvent saleOrderEvent) {
        log.info("CreedConsumerAuthoritiesEvent:" + saleOrderEvent);
    }
}

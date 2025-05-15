package com.ethan.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 2/4/25
 */
@Service
@Slf4j
public class ThreadRetryService {
    @Retryable(value = {IllegalStateException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000L, maxDelay = 2000L))
    public void sendReceive(String senderDestinationName, String receiverDestinationName, String xmlMessage, Consumer<String> process) {
        log.info("sendReceive:{}", senderDestinationName);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // throw new IllegalStateException("not a valid state for testing");
        System.out.println("send success");
    }

    // @Recover
    // public void onExceptionRecovery(IllegalStateException exception, String senderDestinationName, String receiverDestinationName, String xmlMessage, Consumer<String> process) {
    //     log.info("onExceptionRecovery:", exception);
    // }

    @Recover
    public Optional<Object> onExceptionRecovery(IllegalStateException exception, String senderDestinationName, String receiverDestinationName, String xmlMessage) {
        log.info("Inside IllegalStateException recovery");
        throw new IllegalArgumentException(exception.getMessage());
    }
}

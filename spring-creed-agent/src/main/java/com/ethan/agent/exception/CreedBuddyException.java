package com.ethan.agent.exception;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
public class CreedBuddyException extends RuntimeException {
    public CreedBuddyException() {
    }

    public CreedBuddyException(String message) {
        super(message);
    }

    public CreedBuddyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreedBuddyException(Throwable cause) {
        super(cause);
    }

    public CreedBuddyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

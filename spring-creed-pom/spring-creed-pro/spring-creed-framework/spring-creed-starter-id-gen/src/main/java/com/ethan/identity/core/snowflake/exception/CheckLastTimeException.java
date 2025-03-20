package com.ethan.identity.core.snowflake.exception;

public class CheckLastTimeException extends RuntimeException {
    public CheckLastTimeException(String msg){
        super(msg);
    }
}

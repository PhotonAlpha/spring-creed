package com.ethan.example.exception;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 9/5/25
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

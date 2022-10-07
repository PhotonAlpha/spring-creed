package com.ethan.std.exception;

import org.springframework.security.web.csrf.CsrfException;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/18/2022 5:59 PM
 */
public class InvalidSignatureException extends CsrfException {
    public InvalidSignatureException(String message) {
        super("Invalid Signature :"+message);
    }
}

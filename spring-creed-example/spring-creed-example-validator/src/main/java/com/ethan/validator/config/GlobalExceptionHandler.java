package com.ethan.validator.config;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({BindException.class, HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(Exception e) {
        log.error("BindException:", e);
        var errorMessages = new ArrayList<String>();
        if (e instanceof BindException ex) {
            List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
            for (FieldError error : fieldErrors) {
                errorMessages.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorMessages);
        }
        if (e instanceof HandlerMethodValidationException ex) {
            List<ParameterValidationResult> allValidationResults = ex.getAllValidationResults();
            for (ParameterValidationResult result : allValidationResults) {
                List<MessageSourceResolvable> resolvableErrors = result.getResolvableErrors();
                for (MessageSourceResolvable resolvableError : resolvableErrors) {
                    errorMessages.add(resolvableError.getDefaultMessage());
                }
            }
            return ResponseEntity.badRequest().body(errorMessages);
        }
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({jakarta.validation.ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(ValidationException e) {
        log.error("ValidationException:", e);
        var errorMessages = Collections.singleton(ExceptionUtils.getMessage(e.getCause()));
        return ResponseEntity.badRequest().body(errorMessages);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(Exception ex) {
        log.error("handleExceptions:", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

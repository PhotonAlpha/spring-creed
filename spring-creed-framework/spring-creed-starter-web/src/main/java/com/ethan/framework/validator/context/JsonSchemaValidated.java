package com.ethan.framework.validator.context;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Constraint(validatedBy = JsonSchemaValidator.class)
public @interface JsonSchemaValidated {
    String message() default "no schema detected";

    Class<?>[] groups() default {};

    String schemaUri() default "";

    Class<? extends Payload>[] payload() default {};

    Class<? extends AbstractBusinessValidator<?>>[] constraints() default {};
}

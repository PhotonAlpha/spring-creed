/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.framework.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @description: dep-be
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 4/6/2023 12:02 PM
 */
@Deprecated(forRemoval = true)
public abstract class BaseValidator implements Validator {
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "id", "id.empty", "Id is mandatory");
    }
}

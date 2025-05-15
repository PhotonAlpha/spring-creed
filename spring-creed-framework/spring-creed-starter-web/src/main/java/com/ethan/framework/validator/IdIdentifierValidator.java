/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.framework.validator;


import com.ethan.framework.validator.groups.ReferenceNumGroup;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
// use SCOPE_PROTOTYPE to solve the concurrent issue
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class IdIdentifierValidator implements ConstraintValidator<IdIdentifier, Object> {

    private IdIdentifier idIdentifierAnnotation;

    @Override
    public void initialize(IdIdentifier constraintAnnotation) {
        idIdentifierAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?>[] groups = idIdentifierAnnotation.groups();
        if (ArrayUtils.isEmpty(groups)) {
            log.info("default groups");
            return true;
        } else if (Arrays.asList(groups).contains(ReferenceNumGroup.class)) {
            log.info("ReferenceNumGroup groups");
            return true;
        }
        return false;
    }
}

package com.ethan.validator.context;

import com.networknt.schema.ValidationMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Slf4j
public abstract class AbstractBusinessValidator<T> implements ConstraintValidator<BusinessValidated, T> {

    @Override
    public void initialize(BusinessValidated constraintAnnotation) {
        // nothing todo
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        HibernateConstraintValidatorContext validatorContext = context.unwrap(HibernateConstraintValidatorContext.class);
        validatorContext.disableDefaultConstraintViolation();
        List<ValidationMessage> errors = getConstraintViolation(value);
        if (CollectionUtils.isEmpty(errors)) {
            return true;
        }
        for (ValidationMessage vm : getConstraintViolation(value)) {
            validatorContext.buildConstraintViolationWithTemplate(vm.getMessage()).addConstraintViolation();
        }
        return false;
    }


    abstract List<ValidationMessage> getConstraintViolation(T value);
}

package com.ethan.framework.validator;

import com.ethan.framework.validator.context.AbstractBusinessValidator;
import com.networknt.schema.ValidationMessage;

import java.util.List;
import java.util.Objects;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 6/3/25
 */
public class CreedIdValidatorExample extends AbstractBusinessValidator<Object> {

    /**
     * in Controller we can use
     * @JsonSchemaValidated(schemaUri = "my-account-update", constraints = CreedIdValidator.class) @RequestBody MyAccountDetailsVO artisan
     * @param value
     * @return
     */
    @Override
    protected List<ValidationMessage> getConstraintViolation(Object value) {
        /* String id = value.getId();
        if (Objects.nonNull(artisanDao.findById(id))) {
            ValidationMessage build = ValidationMessage.builder().message("user existing already!").build();
            return List.of(build);
        } */
        return List.of();
    }
}

package com.ethan.validator.context;

import com.ethan.validator.controller.vo.MyAccountDetailsVO;
import com.ethan.validator.repository.ArtisanDao;
import com.networknt.schema.ValidationMessage;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 6/3/25
 */
public class CreedIdValidator extends AbstractBusinessValidator<MyAccountDetailsVO> {
    private final ArtisanDao artisanDao;

    public CreedIdValidator(ArtisanDao artisanDao) {
        this.artisanDao = artisanDao;
    }

    @Override
    List<ValidationMessage> getConstraintViolation(MyAccountDetailsVO value) {
        String id = value.getId();
        if (Objects.nonNull(artisanDao.findById(id))) {
            ValidationMessage build = ValidationMessage.builder().message("user existing already!").build();
            return List.of(build);
        }
        return List.of();
    }
}

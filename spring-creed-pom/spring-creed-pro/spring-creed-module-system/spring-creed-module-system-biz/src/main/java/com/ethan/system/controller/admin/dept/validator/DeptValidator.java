package com.ethan.system.controller.admin.dept.validator;

import com.ethan.framework.validator.BaseValidator;
import com.ethan.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@Deprecated(forRemoval = true)
public class DeptValidator extends BaseValidator {
    @Override
    public boolean supports(Class<?> clazz) {
        return DeptSaveReqVO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof DeptSaveReqVO) {
            DeptSaveReqVO reqVO = (DeptSaveReqVO) target;
            if (StringUtils.isBlank(reqVO.getName())) {
                errors.rejectValue("name", "name.null", "name is mandatory");
            }
        }
    }
}

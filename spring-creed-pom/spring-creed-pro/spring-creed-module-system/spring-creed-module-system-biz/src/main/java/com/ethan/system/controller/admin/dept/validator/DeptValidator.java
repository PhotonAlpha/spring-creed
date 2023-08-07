package com.ethan.system.controller.admin.dept.validator;

import com.ethan.framework.validator.BaseValidator;
import com.ethan.system.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class DeptValidator extends BaseValidator {
    @Override
    public boolean supports(Class<?> clazz) {
        return DeptUpdateReqVO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof DeptUpdateReqVO) {
            DeptUpdateReqVO reqVO = (DeptUpdateReqVO) target;
            if (StringUtils.isBlank(reqVO.getName())) {
                errors.rejectValue("name", "name.null", "name is mandatory");
            }
        }
    }
}

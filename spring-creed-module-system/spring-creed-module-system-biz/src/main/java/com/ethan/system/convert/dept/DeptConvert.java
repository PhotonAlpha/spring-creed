package com.ethan.system.convert.dept;

import com.ethan.common.converter.BasicConvert;
import com.ethan.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.ethan.system.dal.entity.dept.SystemDepts;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeptConvert extends BasicConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    SystemDepts convert(DeptSaveReqVO req);
    DeptRespVO convert(SystemDepts bean);

    void update(DeptSaveReqVO bean, @MappingTarget SystemDepts depts);

    List<DeptRespVO> convert(List<SystemDepts> list);

    List<DeptSimpleRespVO> convert0(List<SystemDepts> list);
}

package com.ethan.system.convert.tenant;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantCreateReqVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantExcelVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserCreateReqVO;
import com.ethan.system.dal.entity.tenant.TenantDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 租户 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    TenantDO convert(TenantCreateReqVO bean);

    TenantDO convert(TenantUpdateReqVO bean);

    TenantRespVO convert(TenantDO bean);

    List<TenantRespVO> convertList(List<TenantDO> list);

    PageResult<TenantRespVO> convertPage(PageResult<TenantDO> page);

    List<TenantExcelVO> convertList02(List<TenantDO> list);

    default UserCreateReqVO convert02(TenantCreateReqVO bean) {
        UserCreateReqVO reqVO = new UserCreateReqVO();
        reqVO.setUsername(bean.getUsername());
        reqVO.setPassword(bean.getPassword());
        reqVO.setNickname(bean.getContactName()).setPhone(bean.getContactMobile());
        return reqVO;
    }

}

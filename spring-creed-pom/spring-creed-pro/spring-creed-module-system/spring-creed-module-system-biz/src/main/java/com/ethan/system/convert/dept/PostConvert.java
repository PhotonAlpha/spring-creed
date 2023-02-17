package com.ethan.system.convert.dept;

import com.ethan.system.controller.admin.dept.vo.post.PostCreateReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostExcelVO;
import com.ethan.system.controller.admin.dept.vo.post.PostRespVO;
import com.ethan.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.ethan.system.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.ethan.system.dal.entity.dept.PostDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface PostConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    List<PostSimpleRespVO> convertList02(List<PostDO> list);

    Page<PostRespVO> convertPage(Page<PostDO> page);

    PostRespVO convert(PostDO id);

    PostDO convert(PostCreateReqVO bean);

    PostDO convert(PostUpdateReqVO reqVO);

    List<PostExcelVO> convertList03(List<PostDO> list);

}

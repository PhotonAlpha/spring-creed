package com.ethan.system.convert.dept;

import com.ethan.common.converter.BasicConvert;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.dept.vo.post.PostRespVO;
import com.ethan.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.ethan.system.dal.entity.dept.SystemPosts;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostConvert extends BasicConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    SystemPosts convert(PostSaveReqVO req);
    PostRespVO convert(SystemPosts bean);
    List<PostRespVO> convert(List<SystemPosts> bean);
    PostSimpleRespVO convert0(SystemPosts bean);
    List<PostSimpleRespVO> convert0(List<SystemPosts> bean);

    PageResult<PostRespVO> convertPage(PageResult<SystemPosts> postPage);
}

package com.ethan.app.mapper;

import com.ethan.app.dto.BlogDTO;
import com.ethan.entity.BlogDO;
import com.ethan.vo.BlogVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogMapper {

  // @Mapping(source = "bloggerId", target = "bloggerId")
  BlogVO blogToVo(BlogDO aDo);

  BlogDO blogToDo(BlogDTO vo);

  List<BlogVO> blogListToVo(List<BlogDO> aDo);
}

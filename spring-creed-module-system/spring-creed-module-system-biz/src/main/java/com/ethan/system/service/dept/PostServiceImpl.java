package com.ethan.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.ethan.system.convert.dept.PostConvert;
import com.ethan.system.dal.entity.dept.SystemPosts;
import com.ethan.system.dal.repository.dept.SystemPostsRepository;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.common.utils.collection.CollUtils.convertMap;
import static com.ethan.system.constant.ErrorCodeConstants.POST_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.POST_NAME_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.POST_NOT_ENABLE;
import static com.ethan.system.constant.ErrorCodeConstants.POST_NOT_FOUND;


/**
 * 岗位 Service 实现类
 *
 * 
 */
@Service
@Validated
public class PostServiceImpl implements PostService {

    @Resource
    private SystemPostsRepository postRepository;

    @Override
    public Long createPost(PostSaveReqVO reqVO) {
        // 校验正确性
        validateCreateOrUpdate(null, reqVO.getName(), reqVO.getCode());
        // 插入岗位
        SystemPosts post = PostConvert.INSTANCE.convert(reqVO);
        postRepository.save(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostSaveReqVO reqVO) {
        // 校验正确性
        this.validateCreateOrUpdate(reqVO.getId(), reqVO.getName(), reqVO.getCode());
        // 更新岗位
        SystemPosts updateObj = PostConvert.INSTANCE.convert(reqVO);
        postRepository.save(updateObj);
    }

    @Override
    public void deletePost(Long id) {
        // 校验是否存在
        validatePostExists(id);
        // 删除部门
        postRepository.deleteById(id);
    }


    @Override
    public List<SystemPosts> getPostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return postRepository.findAllById(ids);
    }

    @Override
    public List<SystemPosts> getPostList(Collection<Long> ids, Collection<Integer> statuses) {
        return postRepository.findByIdInAndEnabledIn(ids, statuses);
    }

    @Override
    public void validatePostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<SystemPosts> posts = postRepository.findAllById(ids);
        Map<Long, SystemPosts> postMap = convertMap(posts, SystemPosts::getId);
        // 校验
        ids.forEach(id -> {
            SystemPosts post = postMap.get(id);
            if (post == null) {
                throw exception(POST_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.equals(post.getEnabled())) {
                throw exception(POST_NOT_ENABLE, post.getName());
            }
        });
    }

    @Override
    public PageResult<SystemPosts> getPostPage(PostPageReqVO reqVO) {
        Page<SystemPosts> page = postRepository.findByCondition(reqVO);
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public SystemPosts getPost(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    private void validateCreateOrUpdate(Long id, String name, String code) {
        // 校验自己存在
        validatePostExists(id);
        // 校验岗位名的唯一性
        validatePostNameUnique(id, name);
        // 校验岗位编码的唯一性
        validatePostCodeUnique(id, code);
    }

    private void validatePostNameUnique(Long id, String name) {
        Optional<SystemPosts> postOptional = postRepository.findByName(name);
        if (postOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_NAME_DUPLICATE);
        }
        if (postOptional.map(SystemPosts::getId).map(id::equals).isEmpty()) {
            throw exception(POST_NAME_DUPLICATE);
        }
    }

    private void validatePostCodeUnique(Long id, String code) {
        Optional<SystemPosts> postOptional = postRepository.findByCode(code);
        if (postOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_CODE_DUPLICATE);
        }
        if (postOptional.map(SystemPosts::getId).map(id::equals).isEmpty()) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        Optional<SystemPosts> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw exception(POST_NOT_FOUND);
        }
    }
}

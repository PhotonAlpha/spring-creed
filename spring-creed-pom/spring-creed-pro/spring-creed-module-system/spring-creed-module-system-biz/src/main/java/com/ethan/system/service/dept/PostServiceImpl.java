package com.ethan.system.service.dept;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.dept.vo.post.PostCreateReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostExportReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.ethan.system.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.ethan.system.convert.dept.PostConvert;
import com.ethan.system.dal.entity.dept.PostDO;
import com.ethan.system.dal.repository.dept.PostRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private PostRepository postRepository;

    @Override
    public Long createPost(PostCreateReqVO reqVO) {
        // 校验正确性
        this.checkCreateOrUpdate(null, reqVO.getName(), reqVO.getCode());
        // 插入岗位
        PostDO post = PostConvert.INSTANCE.convert(reqVO);
        postRepository.save(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostUpdateReqVO reqVO) {
        // 校验正确性
        this.checkCreateOrUpdate(reqVO.getId(), reqVO.getName(), reqVO.getCode());
        // 更新岗位
        PostDO updateObj = PostConvert.INSTANCE.convert(reqVO);
        postRepository.save(updateObj);
    }

    @Override
    public void deletePost(Long id) {
        // 校验是否存在
        this.checkPostExists(id);
        // 删除部门
        postRepository.deleteById(id);
    }

    @Override
    public List<PostDO> getPosts(Collection<Long> ids, Collection<Integer> statuses) {
        return postRepository.findByIdInAndStatusIn(ids, statuses);
    }

    @Override
    public PageResult<PostDO> getPostPage(PostPageReqVO reqVO) {
        Page<PostDO> page = postRepository.findAll(getPotSpecification(reqVO), PageRequest.of(reqVO.getPageNo(), reqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    private static Specification<PostDO> getPotSpecification(PostPageReqVO reqVO) {
        return (Specification<PostDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getCode())) {
                predicateList.add(cb.like(root.get("code"),
                        "%" + reqVO.getCode() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(root.get("name"),
                        "%" + reqVO.getName() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
    private static Specification<PostDO> getPotExportSpecification(PostExportReqVO reqVO) {
        return (Specification<PostDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getCode())) {
                predicateList.add(cb.like(root.get("code"),
                        "%" + reqVO.getCode() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(root.get("name"),
                        "%" + reqVO.getName() + "%"));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }


    @Override
    public List<PostDO> getPosts(PostExportReqVO reqVO) {
        return postRepository.findAll(getPotExportSpecification(reqVO));
    }

    @Override
    public PostDO getPost(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    private void checkCreateOrUpdate(Long id, String name, String code) {
        // 校验自己存在
        checkPostExists(id);
        // 校验岗位名的唯一性
        checkPostNameUnique(id, name);
        // 校验岗位编码的唯一性
        checkPostCodeUnique(id, code);
    }

    private void checkPostNameUnique(Long id, String name) {
        Optional<PostDO> postOptional = postRepository.findByName(name);
        if (postOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_NAME_DUPLICATE);
        }
        if (postOptional.map(PostDO::getId).map(i -> i.equals(id)).isEmpty()) {
            throw exception(POST_NAME_DUPLICATE);
        }
    }

    private void checkPostCodeUnique(Long id, String code) {
        Optional<PostDO> postOptional = postRepository.findByCode(code);
        if (postOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(POST_CODE_DUPLICATE);
        }
        if (postOptional.map(PostDO::getId).map(i -> i.equals(id)).isEmpty()) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    private void checkPostExists(Long id) {
        if (id == null) {
            return;
        }
        Optional<PostDO> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw exception(POST_NOT_FOUND);
        }
    }

    @Override
    public void validPosts(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<PostDO> posts = postRepository.findAllById(ids);
        Map<Long, PostDO> postMap = convertMap(posts, PostDO::getId);
        // 校验
        ids.forEach(id -> {
            PostDO post = postMap.get(id);
            if (post == null) {
                throw exception(POST_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(post.getStatus())) {
                throw exception(POST_NOT_ENABLE, post.getName());
            }
        });
    }
}

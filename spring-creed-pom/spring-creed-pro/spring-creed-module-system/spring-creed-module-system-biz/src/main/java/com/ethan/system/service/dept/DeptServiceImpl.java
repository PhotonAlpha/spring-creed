package com.ethan.system.service.dept;

import cn.hutool.core.collection.CollUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.ethan.system.convert.dept.DeptConvert;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.redis.RedisKeyConstants;
import com.ethan.system.dal.repository.dept.SystemDeptsRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_EXITS_CHILDREN;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_NAME_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_NOT_ENABLE;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_NOT_FOUND;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_PARENT_ERROR;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_PARENT_IS_CHILD;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_PARENT_NOT_EXITS;

/**
 * 部门 Service 实现类
 *
 * 
 */
@Service
@Validated
@Slf4j
public class DeptServiceImpl implements DeptService {
    private static final int RECURSION_THRESHOLD = Short.MAX_VALUE;
    @Resource
    private SystemDeptsRepository deptsRepository;
    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public Long createDept(DeptSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(SystemDepts.PARENT_ID_ROOT);
        }
        // 校验父部门的有效性
        validateParentDept(null, createReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入部门
        SystemDepts dept = DeptConvert.INSTANCE.convert(createReqVO);
        deptsRepository.save(dept);
        return dept.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public void updateDept(DeptSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(SystemDepts.PARENT_ID_ROOT);
        }
        // 校验自己存在
        SystemDepts depts = validateDeptExists(updateReqVO.getId());
        // 校验父部门的有效性
        validateParentDept(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验部门名的唯一性
        validateDeptNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新部门
        DeptConvert.INSTANCE.update(updateReqVO, depts);
        deptsRepository.save(depts);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST,
            allEntries = true) // allEntries 清空所有缓存，因为操作一个部门，涉及到多个缓存
    public void deleteDept(Long id) {
        // 校验是否存在
        validateDeptExists(id);
        // 校验是否有子部门
        if (deptsRepository.existsByParentId(id)) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // 删除部门
        deptsRepository.deleteById(id);
    }

    @VisibleForTesting
    SystemDepts validateDeptExists(Long id) {
        if (id == null) {
            return null;
        }
        return deptsRepository.findById(id).orElseThrow(() -> exception(DEPT_NOT_FOUND));
    }

    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || SystemDepts.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父部门
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        // 2. 父部门不存在
        Optional<SystemDepts> parentDept = deptsRepository.findById(parentId);
        if (parentDept.isEmpty()) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父部门，如果父部门是自己的子部门，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }

        validateParentDeptRecursion(parentDept.map(SystemDepts::getParentId).orElse(null), RECURSION_THRESHOLD);
        /** 这部分逻辑使用递归 validateParentDeptRecursion 替代
         for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentDept.map(SystemDepts::getParentId).orElse(null);
            if (Objects.equals(id, parentId)) {
                throw exception(DEPT_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父部门
            if (parentId == null || SystemDepts.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDept = deptsRepository.findById(parentId);
            if (parentDept.isEmpty()) {
                break;
            }
        } */
    }

    void validateParentDeptRecursion(Long id, int recursionThreshold) {
        // 如果Id是根目录，停止递归
        if (id == null || SystemDepts.PARENT_ID_ROOT.equals(id) || recursionThreshold < 1) {
            return;
        }
        Optional<SystemDepts> parentDept = deptsRepository.findById(id);
        Long pId = parentDept.map(SystemDepts::getParentId).orElse(null);
        if (Objects.equals(id, pId)) {
            throw exception(DEPT_PARENT_IS_CHILD);
        }
        // 继续递归下一级父部门
        validateParentDeptRecursion(pId, recursionThreshold - 1);
    }

    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        Optional<SystemDepts> dept = deptsRepository.findByNameAndParentId(name, parentId);
        if (dept.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明新增，不用比较是否为相同 id 的部门
        if (id == null) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
        if (dept.map(SystemDepts::getId).filter(id::equals).isEmpty()) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    @Override
    public SystemDepts getDept(Long id) {
        return deptsRepository.findById(id).orElse(null);
    }

    @Override
    public List<SystemDepts> getDeptList(Collection<Long> ids) {
        return deptsRepository.findAllById(ids);
    }

    @Override
    public List<SystemDepts> getDeptList(DeptListReqVO reqVO) {
        List<SystemDepts> list = deptsRepository.findByCondition(reqVO);
        list.sort(Comparator.comparing(SystemDepts::getSort));
        return list;
    }

    @Override
    public List<SystemDepts> getChildDeptList(Long id) {
        List<SystemDepts> children = new LinkedList<>();
        // 遍历每一层
        Collection<Long> parentIds = Collections.singleton(id);
        getChildDeptsRecursion(children, parentIds, RECURSION_THRESHOLD);
        /* for (int i = 0; i < Short.MAX_VALUE; i++) { // 使用 Short.MAX_VALUE 避免 bug 场景下，存在死循环, 上述递归方式解决
            // 查询当前层，所有的子部门
            List<SystemDepts> depts = deptsRepository.findByParentId(parentIds);
            // 1. 如果没有子部门，则结束遍历
            if (CollUtil.isEmpty(depts)) {
                break;
            }
            // 2. 如果有子部门，继续遍历
            children.addAll(depts);
            parentIds = CollUtils.convertSet(depts, SystemDepts::getId);
        } */
        return children;
    }

    void getChildDeptsRecursion(List<SystemDepts> children, Collection<Long> ids, int recursionThreshold) {
        log.info("getChildDeptsRecursion ids:{} recursionThreshold:{}", ids, recursionThreshold);
        // 如果Id集合是空，停止递归
        ids = Optional.ofNullable(ids).orElse(Collections.emptySet())
                .stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(ids) || recursionThreshold < 1) {
            return;
        }
        // 查询当前层，所有的子部门
        List<SystemDepts> parentDept = deptsRepository.findByParentId(ids);
        // 1. 如果没有子部门，则结束遍历
        if (CollUtil.isEmpty(parentDept)) {
            return;
        }
        // 2. 如果有子部门，继续遍历
        children.addAll(parentDept);
        Set<Long> parentIds = CollUtils.convertSet(parentDept, SystemDepts::getId);
        // 继续递归下一级父部门
        getChildDeptsRecursion(children, parentIds, recursionThreshold - 1);
    }

    @Override
    // @DataPermission(enable = false) // 禁用数据权限，避免建立不正确的缓存
    @Cacheable(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildDeptIdListFromCache(Long id) {
        List<SystemDepts> children = getChildDeptList(id);
        return CollUtils.convertSet(children, SystemDepts::getId);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得部门信息
        Map<Long, SystemDepts> deptMap = getDeptMap(ids);
        // 校验
        ids.forEach(id -> {
            SystemDepts dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.equals(dept.getEnabled())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }
}

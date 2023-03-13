package com.ethan.system.service.dept;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.constant.ErrorCodeConstants;
import com.ethan.system.constant.dept.DeptIdEnum;
import com.ethan.system.controller.admin.dept.vo.dept.DeptCreateReqVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import com.ethan.system.convert.dept.DeptConvert;
import com.ethan.system.dal.entity.dept.DeptDO;
import com.ethan.system.dal.repository.dept.DeptRepository;
import com.ethan.system.mq.dept.DeptProducer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_EXITS_CHILDREN;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_NAME_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_NOT_ENABLE;
import static com.ethan.system.constant.ErrorCodeConstants.DEPT_NOT_FOUND;

/**
 * 部门 Service 实现类
 *
 * 
 */
@Service
@Validated
@Slf4j
public class DeptServiceImpl implements DeptService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 部门缓存
     * key：部门编号 {@link DeptDO#getId()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @SuppressWarnings("FieldCanBeLocal")
    private volatile Map<Long, DeptDO> deptCache;
    /**
     * 父部门缓存
     * key：部门编号 {@link DeptDO#getParentId()}
     * value: 直接子部门列表
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    private volatile Multimap<Long, DeptDO> parentDeptCache;
    /**
     * 缓存部门的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    private volatile Instant maxUpdateTime;

    @Resource
    private DeptRepository deptRepository;

    @Resource
    private DeptProducer deptProducer;

    @Resource
    @Lazy // 注入自己，所以延迟加载
    private DeptService self;

    @Override
    @PostConstruct
    // @TenantIgnore // 初始化缓存，无需租户过滤
    public synchronized void initLocalCache() {
        // 获取部门列表，如果有更新
        List<DeptDO> deptList = loadDeptIfUpdate(maxUpdateTime);
        if (CollectionUtils.isEmpty(deptList)) {
            return;
        }

        // 构建缓存
        ImmutableMap.Builder<Long, DeptDO> builder = ImmutableMap.builder();
        ImmutableMultimap.Builder<Long, DeptDO> parentBuilder = ImmutableMultimap.builder();
        deptList.forEach(sysRoleDO -> {
            builder.put(sysRoleDO.getId(), sysRoleDO);
            parentBuilder.put(sysRoleDO.getParentId(), sysRoleDO);
        });
        // 设置缓存
        deptCache = builder.build();
        parentDeptCache = parentBuilder.build();
        maxUpdateTime = CollUtils.getMaxValue(deptList, DeptDO::getUpdateTime);
        log.info("[initLocalCache][初始化 Dept 数量为 {}]", deptList.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        self.initLocalCache();
    }

    /**
     * 如果部门发生变化，从数据库中获取最新的全量部门。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前部门的最大更新时间
     * @return 部门列表
     */
    protected List<DeptDO> loadDeptIfUpdate(Instant maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadMenuIfUpdate][首次加载全量部门]");
        } else { // 判断数据库中是否有更新的部门
            if (deptRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadMenuIfUpdate][增量加载全量部门]");
        }
        // 第二步，如果有更新，则从数据库加载所有部门
        return deptRepository.findAll();
    }

    @Override
    public Long createDept(DeptCreateReqVO reqVO) {
        // 校验正确性
        if (reqVO.getParentId() == null) {
            reqVO.setParentId(DeptIdEnum.ROOT.getId());
        }
        checkCreateOrUpdate(null, reqVO.getParentId(), reqVO.getName());
        // 插入部门
        DeptDO dept = DeptConvert.INSTANCE.convert(reqVO);
        deptRepository.save(dept);
        // 发送刷新消息
        deptProducer.sendDeptRefreshMessage();
        return dept.getId();
    }

    @Override
    public void updateDept(DeptUpdateReqVO reqVO) {
        // 校验正确性
        if (reqVO.getParentId() == null) {
            reqVO.setParentId(DeptIdEnum.ROOT.getId());
        }
        checkCreateOrUpdate(reqVO.getId(), reqVO.getParentId(), reqVO.getName());
        // 更新部门
        DeptDO updateObj = DeptConvert.INSTANCE.convert(reqVO);
        deptRepository.save(updateObj);
        // 发送刷新消息
        deptProducer.sendDeptRefreshMessage();
    }

    @Override
    public void deleteDept(Long id) {
        // 校验是否存在
        checkDeptExists(id);
        // 校验是否有子部门
        if (deptRepository.countByParentId(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        // 删除部门
        deptRepository.deleteById(id);
        // 发送刷新消息
        deptProducer.sendDeptRefreshMessage();
    }

    @Override
    public List<DeptDO> getSimpleDepts(DeptListReqVO reqVO) {
        return deptRepository.findAll(getDeptSpecification(reqVO));
    }

    private static Specification<DeptDO> getDeptSpecification(DeptListReqVO reqVO) {
        return (Specification<DeptDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getName())) {
                predicateList.add(cb.like(cb.lower(root.get("name").as(String.class)),
                        "%" + reqVO.getName().toLowerCase() + "%"));
                // predicateList.add(cb.equal(root.get("name"), reqVO.getName()));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<DeptDO> getDeptsByParentIdFromCache(Long parentId, boolean recursive) {
        if (parentId == null) {
            return Collections.emptyList();
        }
        List<DeptDO> result = new ArrayList<>(); // TODO 芋艿：待优化，新增缓存，避免每次遍历的计算
        // 递归，简单粗暴
        this.getDeptsByParentIdFromCache(result, parentId,
                recursive ? Integer.MAX_VALUE : 1, // 如果递归获取，则无限；否则，只递归 1 次
                parentDeptCache);
        return result;
    }

    /**
     * 递归获取所有的子部门，添加到 result 结果
     *
     * @param result 结果
     * @param parentId 父编号
     * @param recursiveCount 递归次数
     * @param parentDeptMap 父部门 Map，使用缓存，避免变化
     */
    private void getDeptsByParentIdFromCache(List<DeptDO> result, Long parentId, int recursiveCount,
                                             Multimap<Long, DeptDO> parentDeptMap) {
        // 递归次数为 0，结束！
        if (recursiveCount == 0) {
            return;
        }
        // 获得子部门
        Collection<DeptDO> depts = parentDeptMap.get(parentId);
        if (CollectionUtils.isEmpty(depts)) {
            return;
        }
        result.addAll(depts);
        // 继续递归
        depts.forEach(dept -> getDeptsByParentIdFromCache(result, dept.getId(),
                recursiveCount - 1, parentDeptMap));
    }

    private void checkCreateOrUpdate(Long id, Long parentId, String name) {
        // 校验自己存在
        checkDeptExists(id);
        // 校验父部门的有效性
        checkParentDeptEnable(id, parentId);
        // 校验部门名的唯一性
        checkDeptNameUnique(id, parentId, name);
    }

    private void checkParentDeptEnable(Long id, Long parentId) {
        if (parentId == null || DeptIdEnum.ROOT.getId().equals(parentId)) {
            return;
        }
        // 不能设置自己为父部门
        if (parentId.equals(id)) {
            throw exception(ErrorCodeConstants.DEPT_PARENT_ERROR);
        }
        // 父岗位不存在
        Optional<DeptDO> deptOptional = deptRepository.findById(parentId);
        if (deptOptional.isEmpty()) {
            throw exception(ErrorCodeConstants.DEPT_PARENT_NOT_EXITS);
        }
        DeptDO dept = deptOptional.get();
        // 父部门被禁用
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
            throw exception(DEPT_NOT_ENABLE);
        }
        // 父部门不能是原来的子部门
        List<DeptDO> children = this.getDeptsByParentIdFromCache(id, true);
        if (children.stream().anyMatch(dept1 -> dept1.getId().equals(parentId))) {
            throw exception(ErrorCodeConstants.DEPT_PARENT_IS_CHILD);
        }
    }

    private void checkDeptExists(Long id) {
        if (id == null) {
            return;
        }
        Optional<DeptDO> deptOptional = deptRepository.findById(id);
        if (deptOptional.isEmpty()) {
            throw exception(DEPT_NOT_FOUND);
        }
    }

    private void checkDeptNameUnique(Long id, Long parentId, String name) {
        Optional<DeptDO> deptOptional = deptRepository.findByParentIdAndName(parentId, name);
        if (deptOptional.isEmpty()) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        if (id == null) {
            throw exception(DEPT_NAME_DUPLICATE);
        }

        if (deptOptional.map(DeptDO::getId).map(i -> i.equals(id)).isEmpty()) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    @Override
    public List<DeptDO> getDepts(Collection<Long> ids) {
        return deptRepository.findAllById(ids);
    }

    @Override
    public DeptDO getDept(Long id) {
        return deptRepository.findById(id).orElse(null);
    }

    @Override
    public void validDepts(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 获得科室信息
        List<DeptDO> depts = deptRepository.findAllById(ids);
        Map<Long, DeptDO> deptMap = CollUtils.convertMap(depts, DeptDO::getId);
        // 校验
        ids.forEach(id -> {
            DeptDO dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }

    @Override
    public List<DeptDO> getSimpleDepts(Collection<Long> ids) {
        return deptRepository.findAllById(ids);
    }

}

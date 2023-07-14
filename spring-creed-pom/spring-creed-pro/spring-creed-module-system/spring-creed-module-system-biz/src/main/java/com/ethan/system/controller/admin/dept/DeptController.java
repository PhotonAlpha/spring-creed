package com.ethan.system.controller.admin.dept;

import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.framework.validator.groups.ReferenceNumGroup;
import com.ethan.system.controller.admin.dept.validator.DeptValidator;
import com.ethan.system.controller.admin.dept.vo.dept.DeptCreateReqVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.ethan.system.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import com.ethan.system.convert.dept.DeptConvert;
import com.ethan.system.dal.entity.dept.DeptDO;
import com.ethan.system.service.dept.DeptService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.ethan.common.common.R.success;


@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated({Default.class, ReferenceNumGroup.class})
public class DeptController {

    @Resource
    private DeptService deptService;

    @Resource
    private DeptValidator deptValidator;
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (Optional.ofNullable(binder.getTarget()).map(Object::getClass).filter(deptValidator::supports).isPresent()) {
            binder.addValidators(deptValidator);
        }
    }

    @PostMapping("create")
    @Schema(name = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public R<Long> createDept(@Validated @RequestBody DeptCreateReqVO reqVO) {
        Long deptId = deptService.createDept(reqVO);
        return success(deptId);
    }

    @PutMapping("update")
    @Schema(name = "更新部门")
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public R<Boolean> updateDept(@Valid @RequestBody DeptUpdateReqVO reqVO) {
        deptService.updateDept(reqVO);
        return success(true);
    }

    @DeleteMapping("delete")
    @Schema(name = "删除部门")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public R<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDept(id);
        return success(true);
    }

    @GetMapping("/list")
    @Schema(name = "获取部门列表")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public R<List<DeptRespVO>> listDepts(DeptListReqVO reqVO) {
        List<DeptDO> list = deptService.getSimpleDepts(reqVO);
        list.sort(Comparator.comparing(DeptDO::getSort));
        return success(DeptConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public R<List<DeptSimpleRespVO>> getSimpleDepts() {
        // 获得部门列表，只要开启状态的
        DeptListReqVO reqVO = new DeptListReqVO();
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        List<DeptDO> list = deptService.getSimpleDepts(reqVO);
        // 排序后，返回给前端
        list.sort(Comparator.comparing(DeptDO::getSort));
        return success(DeptConvert.INSTANCE.convertList02(list));
    }

    @GetMapping("/get")
    @Schema(name = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public R<DeptRespVO> getDept(@RequestParam("id") Long id) {
        return success(DeptConvert.INSTANCE.convert(deptService.getDept(id)));
    }

}

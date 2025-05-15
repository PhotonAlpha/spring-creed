package com.ethan.system.controller.admin.permission;

import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageParam;
import com.ethan.common.pojo.PageResult;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleRespVO;
import com.ethan.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.ethan.system.convert.permission.SystemRolesConvert;
import com.ethan.system.dal.entity.permission.SystemRoles;
import com.ethan.system.service.permission.RoleService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ethan.common.common.R.success;
import static com.ethan.framework.operatelog.constant.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 角色")
@RestController
@RequestMapping("/system/role")
@Validated
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    @Schema(name = "创建角色")
    @PreAuthorize("@ss.hasPermission('system:role:create')")
    public R<Long> createRole(@Valid @RequestBody RoleSaveReqVO reqVO) {
        return success(roleService.createRole(reqVO, null));
    }

    @PutMapping("/update")
    @Schema(name = "修改角色")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public R<Boolean> updateRole(@Valid @RequestBody RoleSaveReqVO reqVO) {
        roleService.updateRole(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除角色")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public R<Boolean> deleteRole(@RequestParam("id") Long id) {
        roleService.deleteRole(id);
        return success(true);
    }

    @GetMapping("/get")
    @Schema(name = "获得角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public R<RoleRespVO> getRole(@RequestParam("id") Long id) {
        SystemRoles role = roleService.getRole(id);
        return success(SystemRolesConvert.INSTANCE.convert(role));
    }

    @GetMapping("/page")
    @Schema(name = "获得角色分页")
    // @PreAuthorize("@ss.hasPermission('system:role:query')")
    // @PreAuthorize("hasPermission('system:role:query')")
    public R<PageResult<RoleRespVO>> getRolePage(RolePageReqVO reqVO) {
        return success(SystemRolesConvert.INSTANCE.convert(roleService.getRolePage(reqVO)));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获取角色精简信息列表", description = "只包含被开启的角色，主要用于前端的下拉选项")
    public R<List<RoleRespVO>> getSimpleRoles() {
        // 获得角色列表，只要开启状态的
        List<SystemRoles> list = roleService.getRoleListByStatus(Collections.singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 排序后，返回给前端
        list.sort(Comparator.comparing(SystemRoles::getSort));
        return success(SystemRolesConvert.INSTANCE.convertList03(list));
    }

    @GetMapping("/export-excel")
    @OperateLog(type = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:role:export')")
    public void export(HttpServletResponse response, @Validated RolePageReqVO reqVO) {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SystemRoles> list = roleService.getRolePage(reqVO).getList();
        List<RoleRespVO> data = SystemRolesConvert.INSTANCE.convertList03(list);
        // 输出
        // ExcelUtils.write(response, "角色数据.xls", "角色列表", RoleExcelVO.class, data);
    }

}

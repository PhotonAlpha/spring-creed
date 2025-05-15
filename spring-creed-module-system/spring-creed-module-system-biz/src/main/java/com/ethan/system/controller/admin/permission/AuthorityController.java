package com.ethan.system.controller.admin.permission;

import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageParam;
import com.ethan.common.pojo.PageResult;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityPageReqVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthorityRespVO;
import com.ethan.system.controller.admin.permission.vo.authority.AuthoritySaveReqVO;
import com.ethan.system.convert.permission.SystemAuthorityConvert;
import com.ethan.system.dal.entity.permission.SystemAuthorities;
import com.ethan.system.service.permission.AuthorityService;
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


/**
 * 根据RBAC权限管理，授权类型管理
 */
@Tag(name = "管理后台 - 数据权限")
@RestController
@RequestMapping("/system/authority")
@Validated
public class AuthorityController {

    @Resource
    private AuthorityService authorityService;

    @PostMapping("/create")
    @Schema(name = "创建权限")
    @PreAuthorize("@ss.hasPermission('system:authority:create')")
    public R<Long> createAuthority(@Valid @RequestBody AuthoritySaveReqVO reqVO) {
        return success(authorityService.createAuthority(reqVO, null));
    }

    @PutMapping("/update")
    @Schema(name = "修改权限")
    @PreAuthorize("@ss.hasPermission('system:authority:update')")
    public R<Boolean> updateAuthority(@Valid @RequestBody AuthoritySaveReqVO reqVO) {
        authorityService.updateAuthority(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除权限")
    @Parameter(name = "id", description = "权限编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:authority:delete')")
    public R<Boolean> deleteAuthority(@RequestParam("id") Long id) {
        authorityService.deleteAuthority(id);
        return success(true);
    }

    @GetMapping("/get")
    @Schema(name = "获得权限信息")
    @PreAuthorize("@ss.hasPermission('system:authority:query')")
    public R<AuthorityRespVO> getAuthority(@RequestParam("id") Long id) {
        var authority = authorityService.getAuthority(id);
        return success(SystemAuthorityConvert.INSTANCE.convert(authority));
    }

    @GetMapping("/page")
    @Schema(name = "获得权限分页")
    // @PreAuthorize("@ss.hasPermission('system:authority:query')")
    // @PreAuthorize("hasPermission('system:authority:query')")
    public R<PageResult<AuthorityRespVO>> getAuthorityPage(AuthorityPageReqVO reqVO) {
        return success(SystemAuthorityConvert.INSTANCE.convert(authorityService.getAuthorityPage(reqVO)));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获取权限精简信息列表", description = "只包含被开启的权限，主要用于前端的下拉选项")
    public R<List<AuthorityRespVO>> getSimpleAuthoritys() {
        // 获得权限列表，只要开启状态的
        var list = authorityService.getAuthorityListByStatus(Collections.singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 排序后，返回给前端
        list.sort(Comparator.comparing(SystemAuthorities::getSort));
        return success(SystemAuthorityConvert.INSTANCE.convertList03(list));
    }

    @GetMapping("/export-excel")
    @OperateLog(type = EXPORT)
    @PreAuthorize("@ss.hasPermission('system:authority:export')")
    public void export(HttpServletResponse response, @Validated AuthorityPageReqVO reqVO) {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        var list = authorityService.getAuthorityPage(reqVO).getList();
        List<AuthorityRespVO> data = SystemAuthorityConvert.INSTANCE.convertList03(list);
        // 输出
        // ExcelUtils.write(response, "权限数据.xls", "权限列表", AuthorityExcelVO.class, data);
    }

}

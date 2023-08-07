/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.controller.admin.tenant;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantCreateReqVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.ethan.system.controller.admin.tenant.vo.tenant.TenantUpdateReqVO;
import com.ethan.system.convert.tenant.TenantConvert;
import com.ethan.system.dal.entity.tenant.TenantDO;
import com.ethan.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ethan.common.common.R.success;

@Tag(name = "TenantController", description = "Tenant Controller")
@RestController
@RequestMapping("/system/tenant")
public class TenantController {
    @Resource
    private TenantService tenantService;


    // @Hidden
    @Operation(summary = "getTenantIdByName", description = "get tenant id by name",
            parameters = {@Parameter(in = ParameterIn.PATH, name = "name", description = "tenant name")}
            /* requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "getTenantIdByName description",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "onBoardingRequest",
                                            summary = "onBoardingRequest sample",
                                            value = "{}"
                                    )
                            }
                    )
            ) */
    )
    @GetMapping("/get-id-by-name")
    @PermitAll
    public R<Long> getTenantIdByName(@RequestParam("name") String name) {
        TenantDO tenant = tenantService.getTenantByName(name);
        return success(tenant != null ? tenant.getId() : null);
    }

    @PostMapping("/create")
    @Operation(summary = "创建租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public R<Long> createTenant(@Valid @RequestBody TenantCreateReqVO createReqVO) {
        return success(tenantService.createTenant(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public R<Boolean> updateTenant(@Valid @RequestBody TenantUpdateReqVO updateReqVO) {
        tenantService.updateTenant(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public R<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantService.deleteTenant(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public R<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        TenantDO tenant = tenantService.getTenant(id);
        return success(TenantConvert.INSTANCE.convert(tenant));
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户分页")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public R<PageResult<TenantRespVO>> getTenantPage(@Valid TenantPageReqVO pageVO) {
        Page<TenantDO> pageResult = tenantService.getTenantPage(pageVO);
        return success(TenantConvert.INSTANCE.convertPage(new PageResult(pageResult.getContent(), pageResult.getTotalElements())));
    }

    /*@GetMapping("/export-excel")
    @Operation(summary = "导出租户 Excel")
    @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    @OperateLog(type = EXPORT)
    public void exportTenantExcel(@Valid TenantExportReqVO exportReqVO,
                                  HttpServletResponse response) throws IOException {
        List<TenantDO> list = tenantService.getTenantList(exportReqVO);
        // 导出 Excel
        List<TenantExcelVO> datas = TenantConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "租户.xls", "数据", TenantExcelVO.class, datas);
    }*/
}

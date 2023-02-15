/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.controller.admin.tenant;

import com.ethan.common.common.R;
import com.ethan.system.dal.entity.tenant.TenantDO;
import com.ethan.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public R<TenantDO> getTenantIdByName(@RequestParam("name") String name) {
        TenantDO tenant = tenantService.getTenantByName(name);
        return R.success(tenant);
    }
}

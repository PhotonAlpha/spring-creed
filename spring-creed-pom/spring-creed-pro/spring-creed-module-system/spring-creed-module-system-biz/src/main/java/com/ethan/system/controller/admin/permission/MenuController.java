package com.ethan.system.controller.admin.permission;


import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuRespVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.ethan.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import com.ethan.system.convert.permission.MenuConvert;
import com.ethan.system.dal.entity.permission.SystemMenus;
import com.ethan.system.service.permission.MenuService;
import com.ethan.system.service.tenant.TenantService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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

import java.util.Comparator;
import java.util.List;

import static com.ethan.common.common.R.success;


@Tag(name = "管理后台 - 菜单")
@RestController
@RequestMapping("/system/menu")
@Validated
public class MenuController {

    @Resource
    private MenuService menuService;
    @Resource
    private TenantService tenantService;

    @PostMapping("/create")
    @Schema(name = "创建菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:create')")
    public R<Long> createMenu(@Valid @RequestBody MenuSaveVO reqVO) {
        Long menuId = menuService.createMenu(reqVO);
        return success(menuId);
    }

    @PutMapping("/update")
    @Schema(name = "修改菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public R<Boolean> updateMenu(@Valid @RequestBody MenuSaveVO reqVO) {
        menuService.updateMenu(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除菜单")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public R<Boolean> deleteMenu(@RequestParam("id") Long id) {
        menuService.deleteMenu(id);
        return success(true);
    }

    @GetMapping("/list")
    @Schema(name = "获取菜单列表", description = "用于【菜单管理】界面")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<List<MenuRespVO>> getMenus(MenuListReqVO reqVO) {
        List<SystemMenus> list = menuService.getMenuList(reqVO);
        list.sort(Comparator.comparing(SystemMenus::getSort));
        return success(MenuConvert.INSTANCE.convert(list));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获取菜单精简信息列表", description = "只包含被开启的菜单，用于【角色分配菜单】功能的选项。" +
            "在多租户的场景下，会只返回租户所在套餐有的菜单")
    public R<List<MenuSimpleRespVO>> getSimpleMenus() {
        // 获得菜单列表，只要开启状态的
        MenuListReqVO reqVO = new MenuListReqVO();
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        List<SystemMenus> list = menuService.getMenuListByTenant(reqVO);
        // 排序后，返回给前端
        list.sort(Comparator.comparing(SystemMenus::getSort));
        return success(MenuConvert.INSTANCE.convert0(list));
    }

    @GetMapping("/get")
    @Schema(name = "获取菜单信息")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public R<MenuRespVO> getMenu(Long id) {
        SystemMenus menu = menuService.getMenu(id);
        return success(MenuConvert.INSTANCE.convert(menu));
    }

}

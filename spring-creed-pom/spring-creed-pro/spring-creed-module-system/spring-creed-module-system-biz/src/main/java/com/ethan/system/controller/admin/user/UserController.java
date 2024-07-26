package com.ethan.system.controller.admin.user;

import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.constant.SexEnum;
import com.ethan.common.pojo.PageParam;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdateStatusReqVO;
import com.ethan.system.convert.user.UserConvert;
import com.ethan.system.dal.entity.dept.SystemDepts;
import com.ethan.system.dal.entity.permission.SystemUsers;
import com.ethan.system.service.dept.DeptService;
import com.ethan.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ethan.common.common.R.success;
import static com.ethan.framework.operatelog.constant.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/system/user")
@Validated
public class UserController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DeptService deptService;

    @PostMapping("/create")
    @Schema(name = "新增用户")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public R<Long> createUser(@Valid @RequestBody UserSaveReqVO reqVO) {
        Long id = userService.createUser(reqVO);
        return success(id);
    }

    @PutMapping("update")
    @Schema(name = "修改用户")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public R<Boolean> updateUser(@Valid @RequestBody UserSaveReqVO reqVO) {
        userService.updateUser(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = String.class))
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public R<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @PutMapping("/update-password")
    @Schema(name = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public R<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(reqVO.getId(), reqVO.getPassword());
        return success(true);
    }

    @PutMapping("/update-status")
    @Schema(name = "修改用户状态")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public R<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusReqVO reqVO) {
        userService.updateUserStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @GetMapping("/page")
    @Schema(name = "获得用户分页列表")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    public R<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO reqVO) {
        // 获得用户分页列表
        PageResult<SystemUsers> pageResult = userService.getUserPage(reqVO);
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal())); // 返回空
        }

        // 获得拼接需要的数据
        Map<Long, SystemDepts> deptMap = deptService.getDeptMap(
                CollUtils.convertList(pageResult.getList(), SystemUsers::getDeptId));
        return success(new PageResult<>(UserConvert.INSTANCE.convertList(pageResult.getList(), deptMap),
                pageResult.getTotal()));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Schema(name =  "获取用户精简信息列表", description = "只包含被开启的用户，主要用于前端的下拉选项")
    public R<List<UserSimpleRespVO>> getSimpleUsers() {
        // 获用户门列表，只要开启状态的
        List<SystemUsers> list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
        // 拼接数据
        Map<Long, SystemDepts> deptMap = deptService.getDeptMap(
                CollUtils.convertList(list, SystemUsers::getDeptId));
        // 排序后，返回给前端
        return success(UserConvert.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/get")
    @Schema(name = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<UserRespVO> getInfo(@RequestParam("id") Long id) {
        SystemUsers user = userService.getUser(id);
        if (user == null) {
            return success(null);
        }
        // 拼接数据
        SystemDepts dept = deptService.getDept(user.getDeptId());
        return success(UserConvert.INSTANCE.convert2UserSaveReq(user, dept));
    }

    @GetMapping("/export")
    @Schema(name = "导出用户")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @OperateLog(type = EXPORT)
    public void exportUsers(@Validated UserPageReqVO reqVO,
                            HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SystemUsers> list = userService.getUserPage(reqVO).getList();
        // 输出 Excel
        Map<Long, SystemDepts> deptMap = deptService.getDeptMap(
                CollUtils.convertList(list, SystemUsers::getDeptId));
        // ExcelUtils.write(response, "用户数据.xls", "数据", UserRespVO.class,
        //         UserConvert.INSTANCE.convertList(list, deptMap));
    }

    @GetMapping("/get-import-template")
    @Schema(name = "获得导入用户模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<UserImportExcelVO> list = Arrays.asList(
                UserImportExcelVO.builder().username("aaa").deptId(1L).email("yunai@iocoder.cn").mobile("15601691300")
                        .nickname("dsa").status(CommonStatusEnum.ENABLE.getStatus()).sex(SexEnum.MALE.getSex()).build(),
                UserImportExcelVO.builder().username("bbb").deptId(2L).email("yuanma@iocoder.cn").mobile("15601701300")
                        .nickname("dsa").status(CommonStatusEnum.DISABLE.getStatus()).sex(SexEnum.FEMALE.getSex()).build()
        );
        // 输出
        // ExcelUtils.write(response, "用户导入模板.xls", "用户列表", UserImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Schema(name = "导入用户")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true, schema = @Schema(implementation = MultipartFile.class)),
            @Parameter(name = "updateSupport", description = "是否支持更新，默认为 false", example = "true",  schema = @Schema(implementation = Boolean.class))
    })
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public R<UserImportRespVO> importExcel(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        List<UserImportExcelVO> list = null;// TODO ExcelUtils.read(file, UserImportExcelVO.class);
        return success(userService.importUserList(list, updateSupport));
    }

}

package com.ethan.system.controller.admin.user;

import com.ethan.common.common.R;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.constant.SexEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.security.websecurity.entity.CreedConsumer;
import com.ethan.system.controller.admin.user.vo.user.UserCreateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserExportReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.ethan.system.controller.admin.user.vo.user.UserImportRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageItemRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserPageReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdatePasswordReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdateReqVO;
import com.ethan.system.controller.admin.user.vo.user.UserUpdateStatusReqVO;
import com.ethan.system.convert.user.UserConvert;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public R<String> createUser(@Valid @RequestBody UserCreateReqVO reqVO) {
        String id = userService.createUser(reqVO);
        return success(id);
    }

    @PutMapping("update")
    @Schema(name = "修改用户")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public R<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO reqVO) {
        userService.updateUser(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = String.class))
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public R<Boolean> deleteUser(@RequestParam("id") String id) {
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
    public R<PageResult<UserPageItemRespVO>> getUserPage(@Valid UserPageReqVO reqVO) {
        // 获得用户分页列表
        PageResult<CreedConsumer> pageResult = userService.getUserPage(reqVO);
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal())); // 返回空
        }

        // 获得拼接需要的数据 TODO
        // Collection<Long> deptIds = convertList(pageResult.getList(), AdminUserDO::getDeptId);
        // Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
        // 拼接结果返回
        List<UserPageItemRespVO> userList = new ArrayList<>(pageResult.getList().size());
        pageResult.getList().forEach(user -> {
            UserPageItemRespVO respVO = UserConvert.INSTANCE.convert(user);
            // respVO.setDept(UserConvert.INSTANCE.convert(deptMap.get(user.getDeptId())));
            userList.add(respVO);
        });
        return success(new PageResult<>(userList, pageResult.getTotal()));
    }

    @GetMapping("/list-all-simple")
    @Schema(name =  "获取用户精简信息列表", description = "只包含被开启的用户，主要用于前端的下拉选项")
    public R<List<UserSimpleRespVO>> getSimpleUsers() {
        // 获用户门列表，只要开启状态的
        List<CreedConsumer> list = userService.getUsersByStatus(CommonStatusEnum.ENABLE);
        // 排序后，返回给前端
        return success(UserConvert.INSTANCE.convertList04(list));
    }

    @GetMapping("/get")
    @Schema(name = "获得用户详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public R<UserRespVO> getInfo(@RequestParam("id") String id) {
        return success(UserConvert.INSTANCE.convert(userService.getUser(id)));
    }

    @GetMapping("/export")
    @Schema(name = "导出用户")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @OperateLog(type = EXPORT)
    public void exportUsers(@Validated UserExportReqVO reqVO,
                            HttpServletResponse response) throws IOException {
        // 获得用户列表
        List<CreedConsumer> users = userService.getUsers(reqVO);

        // 获得拼接需要的数据
        // Collection<Long> deptIds = convertList(users, AdminUserDO::getDeptId);
        // Map<Long, DeptDO> deptMap = deptService.getDeptMap(deptIds);
        // Map<Long, AdminUserDO> deptLeaderUserMap = userService.getUserMap(
        //         convertSet(deptMap.values(), DeptDO::getLeaderUserId));
        // 拼接数据
        List<UserExcelVO> excelUsers = new ArrayList<>(users.size());
        users.forEach(user -> {
            UserExcelVO excelVO = UserConvert.INSTANCE.convert02(user);
            // 设置部门
            // MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> {
            //     excelVO.setDeptName(dept.getName());
            //     // 设置部门负责人的名字
            //     MapUtils.findAndThen(deptLeaderUserMap, dept.getLeaderUserId(),
            //             deptLeaderUser -> excelVO.setDeptLeaderNickname(deptLeaderUser.getNickname()));
            // });
            excelUsers.add(excelVO);
        });

        // 输出
        // ExcelUtils.write(response, "用户数据.xls", "用户列表", UserExcelVO.class, excelUsers);
    }

    @GetMapping("/get-import-template")
    @Schema(name = "获得导入用户模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<UserImportExcelVO> list = Arrays.asList(
                UserImportExcelVO.builder().username("yunai").deptId(1L).email("yunai@iocoder.cn").mobile("15601691300")
                        .nickname("TES").status(CommonStatusEnum.ENABLE.getStatus()).sex(SexEnum.MALE.getSex()).build(),
                UserImportExcelVO.builder().username("yuanma").deptId(2L).email("yuanma@iocoder.cn").mobile("15601701300")
                        .nickname("源码").status(CommonStatusEnum.DISABLE.getStatus()).sex(SexEnum.FEMALE.getSex()).build()
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
        List<UserImportExcelVO> list = Collections.emptyList();//ExcelUtils.read(file, UserImportExcelVO.class);
        return success(userService.importUsers(list, updateSupport));
    }

}

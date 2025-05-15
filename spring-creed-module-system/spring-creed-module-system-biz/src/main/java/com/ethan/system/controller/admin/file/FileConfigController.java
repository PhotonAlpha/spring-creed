package com.ethan.system.controller.admin.file;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.file.vo.config.FileConfigCreateReqVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigPageReqVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigRespVO;
import com.ethan.system.controller.admin.file.vo.config.FileConfigUpdateReqVO;
import com.ethan.system.convert.file.FileConfigConvert;
import com.ethan.system.dal.entity.file.FileConfigDO;
import com.ethan.system.service.file.FileConfigService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

import static com.ethan.common.common.R.success;


@Tag(name = "管理后台 - 文件配置")
@RestController
@RequestMapping("/infra/file-config")
@Validated
public class FileConfigController {

    @Resource
    private FileConfigService fileConfigService;

    @PostMapping("/create")
    @Schema(name = "创建文件配置")
    @PreAuthorize("@ss.hasPermission('infra:file-config:create')")
    public R<Long> createFileConfig(@Valid @RequestBody FileConfigCreateReqVO createReqVO) {
        return success(fileConfigService.createFileConfig(createReqVO));
    }

    @PutMapping("/update")
    @Schema(name = "更新文件配置")
    @PreAuthorize("@ss.hasPermission('infra:file-config:update')")
    public R<Boolean> updateFileConfig(@Valid @RequestBody FileConfigUpdateReqVO updateReqVO) {
        fileConfigService.updateFileConfig(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-master")
    @Schema(name = "更新文件配置为 Master")
    @PreAuthorize("@ss.hasPermission('infra:file-config:update')")
    public R<Boolean> updateFileConfigMaster(@RequestParam("id") Long id) {
        fileConfigService.updateFileConfigMaster(id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除文件配置")
    @Parameter(name = "id", description = "编号", required = true, schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('infra:file-config:delete')")
    public R<Boolean> deleteFileConfig(@RequestParam("id") Long id) {
        fileConfigService.deleteFileConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Schema(name = "获得文件配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public R<FileConfigRespVO> getFileConfig(@RequestParam("id") Long id) {
        FileConfigDO fileConfig = fileConfigService.getFileConfig(id);
        return success(FileConfigConvert.INSTANCE.convert(fileConfig));
    }

    @GetMapping("/page")
    @Schema(name = "获得文件配置分页")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public R<PageResult<FileConfigRespVO>> getFileConfigPage(@Valid FileConfigPageReqVO pageVO) {
        Page<FileConfigDO> pageResult = fileConfigService.getFileConfigPage(pageVO);
        PageResult<FileConfigDO> res = new PageResult<FileConfigDO>(pageResult.getContent(), pageResult.getTotalElements());
        return success(FileConfigConvert.INSTANCE.convertPage(res));
    }

    @GetMapping("/test")
    @Schema(name = "测试文件配置是否正确")
    @PreAuthorize("@ss.hasPermission('infra:file-config:query')")
    public R<String> testFileConfig(@RequestParam("id") Long id) throws Exception {
        String url = fileConfigService.testFileConfig(id);
        return success(url);
    }
}

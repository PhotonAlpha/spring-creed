package com.ethan.system.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.servlet.ServletUtils;
import com.ethan.system.controller.admin.file.vo.file.FilePageReqVO;
import com.ethan.system.controller.admin.file.vo.file.FileRespVO;
import com.ethan.system.convert.file.FileConvert;
import com.ethan.system.dal.entity.file.FileDO;
import com.ethan.system.service.file.FileService;
import com.ethan.framework.operatelog.annotations.OperateLog;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.ethan.common.common.R.success;

@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Schema(name = "上传文件")
    @Parameters({
            @Parameter(name = "file", description = "文件附件", required = true),
            @Parameter(name = "path", description = "文件路径", example = "yudaoyuanma.png")
    })
    @OperateLog(logArgs = false) // 上传文件，没有记录操作日志的必要
    public R<String> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam(name = "path", required = false) String path) throws Exception {
        return success(fileService.createFile(file.getOriginalFilename(), path, IoUtil.readBytes(file.getInputStream())));
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public R<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
        fileService.deleteFile(id);
        return success(true);
    }

    @GetMapping("/{configId}/get/{path}")
    @PermitAll
    @Schema(name = "下载文件")
    @Parameters({
            @Parameter(name = "configId", description = "配置编号",  required = true),
            @Parameter(name = "path", description = "文件路径", required = true)
    })
    public void getFileContent(HttpServletResponse response,
                               @PathVariable("configId") Long configId,
                               @PathVariable("path") String path) throws Exception {
        byte[] content = fileService.getFileContent(configId, path);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        ServletUtils.writeAttachment(response, path, content);
    }

    @GetMapping("/page")
    @Schema(name = "获得文件分页")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public R<PageResult<FileRespVO>> getFilePage(@Valid FilePageReqVO pageVO) {
        Page<FileDO> pageResult = fileService.getFilePage(pageVO);
        PageResult<FileDO> res = new PageResult<>(pageResult.getContent(), pageResult.getTotalElements());
        return success(FileConvert.INSTANCE.convertPage(res));
    }

}

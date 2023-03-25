package com.ethan.system.controller.admin.dict;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeCreateReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeExcelVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeExportReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.ethan.system.controller.admin.dict.vo.type.DictTypeUpdateReqVO;
import com.ethan.system.convert.dict.DictTypeConvert;
import com.ethan.system.dal.entity.dict.DictTypeDO;
import com.ethan.system.service.dict.DictTypeService;
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

import java.io.IOException;
import java.util.List;

import static com.ethan.common.common.R.success;
import static com.ethan.framework.operatelog.constant.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 字典类型")
@RestController
@RequestMapping("/system/dict-type")
@Validated
public class DictTypeController {

    @Resource
    private DictTypeService dictTypeService;

    @PostMapping("/create")
    @Schema(name ="创建字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public R<Long> createDictType(@Valid @RequestBody DictTypeCreateReqVO reqVO) {
        Long dictTypeId = dictTypeService.createDictType(reqVO);
        return success(dictTypeId);
    }

    @PutMapping("/update")
    @Schema(name ="修改字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public R<Boolean> updateDictType(@Valid @RequestBody DictTypeUpdateReqVO reqVO) {
        dictTypeService.updateDictType(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name ="删除字典类型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public R<Boolean> deleteDictType(Long id) {
        dictTypeService.deleteDictType(id);
        return success(true);
    }

    @Schema(name ="/获得字典类型的分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public R<PageResult<DictTypeRespVO>> pageDictTypes(@Valid DictTypePageReqVO reqVO) {
        return success(DictTypeConvert.INSTANCE.convertPage(dictTypeService.getDictTypePage(reqVO)));
    }

    @Schema(name ="/查询字典类型详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @GetMapping(value = "/get")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public R<DictTypeRespVO> getDictType(@RequestParam("id") Long id) {
        return success(DictTypeConvert.INSTANCE.convert(dictTypeService.getDictType(id)));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获得全部字典类型列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public R<List<DictTypeSimpleRespVO>> listSimpleDictTypes() {
        List<DictTypeDO> list = dictTypeService.getDictTypeList();
        return success(DictTypeConvert.INSTANCE.convertList(list));
    }

    @Schema(name ="导出数据类型")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @OperateLog(type = EXPORT)
    public void export(HttpServletResponse response, @Valid DictTypeExportReqVO reqVO) throws IOException {
        List<DictTypeDO> list = dictTypeService.getDictTypeList(reqVO);
        List<DictTypeExcelVO> data = DictTypeConvert.INSTANCE.convertList02(list);
        // 输出
        // ExcelUtils.write(response, "字典类型.xls", "类型列表", DictTypeExcelVO.class, data);
    }

}

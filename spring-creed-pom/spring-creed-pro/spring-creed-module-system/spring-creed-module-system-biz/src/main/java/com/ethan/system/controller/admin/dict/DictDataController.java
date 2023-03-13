package com.ethan.system.controller.admin.dict;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.framework.operatelog.annotations.OperateLog;
import com.ethan.system.controller.admin.dict.vo.data.DictDataCreateReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataExcelVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.ethan.system.controller.admin.dict.vo.data.DictDataUpdateReqVO;
import com.ethan.system.convert.dict.DictDataConvert;
import com.ethan.system.dal.entity.dict.DictDataDO;
import com.ethan.system.service.dict.DictDataService;
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

@Tag(name = "管理后台 - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class DictDataController {

    @Resource
    private DictDataService dictDataService;

    @PostMapping("/create")
    @Schema(name ="新增字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public R<Long> createDictData(@Valid @RequestBody DictDataCreateReqVO reqVO) {
        Long dictDataId = dictDataService.createDictData(reqVO);
        return success(dictDataId);
    }

    @PutMapping("update")
    @Schema(name ="修改字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public R<Boolean> updateDictData(@Valid @RequestBody DictDataUpdateReqVO reqVO) {
        dictDataService.updateDictData(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除字典数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public R<Boolean> deleteDictData(Long id) {
        dictDataService.deleteDictData(id);
        return success(true);
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获得全部字典数据列表", description = "一般用于管理后台缓存字典数据在本地")
    // 无需添加权限认证，因为前端全局都需要
    public R<List<DictDataSimpleRespVO>> getSimpleDictDatas() {
        List<DictDataDO> list = dictDataService.getDictDatas();
        return success(DictDataConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Schema(name ="/获得字典类型的分页列表")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    // @PreAuthorize("hasAnyAuthority('system:dict:query')")
    public R<PageResult<DictDataRespVO>> getDictTypePage(@Valid DictDataPageReqVO reqVO) {
        return success(DictDataConvert.INSTANCE.convertPage(dictDataService.getDictDataPage(reqVO)));
    }

    @GetMapping(value = "/get")
    @Schema(name ="/查询字典数据详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024", schema = @Schema(implementation = Long.class))
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public R<DictDataRespVO> getDictData(@RequestParam("id") Long id) {
        return success(DictDataConvert.INSTANCE.convert(dictDataService.getDictData(id)));
    }

    @GetMapping("/export")
    @Schema(name ="导出字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:export')")
    @OperateLog(type = EXPORT)
    public void export(HttpServletResponse response, @Valid DictDataExportReqVO reqVO) throws IOException {
        List<DictDataDO> list = dictDataService.getDictDatas(reqVO);
        List<DictDataExcelVO> data = DictDataConvert.INSTANCE.convertList02(list);
        // 输出
        // ExcelUtils.write(response, "字典数据.xls", "数据列表", DictDataExcelVO.class, data);
    }

}

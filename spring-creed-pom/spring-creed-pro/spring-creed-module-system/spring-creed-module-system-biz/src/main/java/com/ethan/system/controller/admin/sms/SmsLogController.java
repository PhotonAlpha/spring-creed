package com.ethan.system.controller.admin.sms;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogExcelVO;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogExportReqVO;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogRespVO;
import com.ethan.system.convert.sms.SmsLogConvert;
import com.ethan.system.dal.entity.sms.SmsLogDO;
import com.ethan.system.service.sms.SmsLogService;
import com.ethan.framework.operatelog.annotations.OperateLog;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.ethan.common.common.R.success;
import static com.ethan.framework.operatelog.constant.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 短信日志")
@RestController
@RequestMapping("/system/sms-log")
@Validated
public class SmsLogController {

    @Resource
    private SmsLogService smsLogService;

    @GetMapping("/page")
    @Schema(name="获得短信日志分页")
    @PreAuthorize("@ss.hasPermission('system:sms-log:query')")
    public R<PageResult<SmsLogRespVO>> getSmsLogPage(@Valid SmsLogPageReqVO pageVO) {
        PageResult<SmsLogDO> pageResult = smsLogService.getSmsLogPage(pageVO);
        return success(SmsLogConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Schema(name="导出短信日志 Excel")
    @PreAuthorize("@ss.hasPermission('system:sms-log:export')")
    @OperateLog(type = EXPORT)
    public void exportSmsLogExcel(@Valid SmsLogExportReqVO exportReqVO,
                                  HttpServletResponse response) throws IOException {
        List<SmsLogDO> list = smsLogService.getSmsLogList(exportReqVO);
        // 导出 Excel
        List<SmsLogExcelVO> datas = SmsLogConvert.INSTANCE.convertList02(list);
        // ExcelUtils.write(response, "短信日志.xls", "数据", SmsLogExcelVO.class, datas);
    }

}

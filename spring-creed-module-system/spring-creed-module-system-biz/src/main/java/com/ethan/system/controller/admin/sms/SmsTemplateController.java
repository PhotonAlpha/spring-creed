package com.ethan.system.controller.admin.sms;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateCreateReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateExcelVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateExportReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateRespVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateSendReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateUpdateReqVO;
import com.ethan.system.convert.sms.SmsTemplateConvert;
import com.ethan.system.dal.entity.sms.SmsTemplateDO;
import com.ethan.system.service.sms.SmsSendService;
import com.ethan.system.service.sms.SmsTemplateService;
import com.ethan.framework.operatelog.annotations.OperateLog;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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


@Tag(name = "管理后台 - 短信模板")
@RestController
@RequestMapping("/system/sms-template")
public class SmsTemplateController {

    @Resource
    private SmsTemplateService smsTemplateService;
    @Resource
    private SmsSendService smsSendService;

    @PostMapping("/create")
    @Schema(name="创建短信模板")
    @PreAuthorize("@ss.hasPermission('system:sms-template:create')")
    public R<Long> createSmsTemplate(@Valid @RequestBody SmsTemplateCreateReqVO createReqVO) {
        return success(smsTemplateService.createSmsTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Schema(name="更新短信模板")
    @PreAuthorize("@ss.hasPermission('system:sms-template:update')")
    public R<Boolean> updateSmsTemplate(@Valid @RequestBody SmsTemplateUpdateReqVO updateReqVO) {
        smsTemplateService.updateSmsTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name="删除短信模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-template:delete')")
    public R<Boolean> deleteSmsTemplate(@RequestParam("id") Long id) {
        smsTemplateService.deleteSmsTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Schema(name="获得短信模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sms-template:query')")
    public R<SmsTemplateRespVO> getSmsTemplate(@RequestParam("id") Long id) {
        SmsTemplateDO smsTemplate = smsTemplateService.getSmsTemplate(id);
        return success(SmsTemplateConvert.INSTANCE.convert(smsTemplate));
    }

    @GetMapping("/page")
    @Schema(name="获得短信模板分页")
    @PreAuthorize("@ss.hasPermission('system:sms-template:query')")
    public R<PageResult<SmsTemplateRespVO>> getSmsTemplatePage(@Valid SmsTemplatePageReqVO pageVO) {
        PageResult<SmsTemplateDO> pageResult = smsTemplateService.getSmsTemplatePage(pageVO);
        return success(SmsTemplateConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Schema(name="导出短信模板 Excel")
    @PreAuthorize("@ss.hasPermission('system:sms-template:export')")
    @OperateLog(type = EXPORT)
    public void exportSmsTemplateExcel(@Valid SmsTemplateExportReqVO exportReqVO,
                                       HttpServletResponse response) throws IOException {
        List<SmsTemplateDO> list = smsTemplateService.getSmsTemplateList(exportReqVO);
        // 导出 Excel
        List<SmsTemplateExcelVO> datas = SmsTemplateConvert.INSTANCE.convertList02(list);
        // ExcelUtils.write(response, "短信模板.xls", "数据", SmsTemplateExcelVO.class, datas);
    }

    @PostMapping("/send-sms")
    @Schema(name="发送短信")
    @PreAuthorize("@ss.hasPermission('system:sms-template:send-sms')")
    public R<Long> sendSms(@Valid @RequestBody SmsTemplateSendReqVO sendReqVO) {
        return success(smsSendService.sendSingleSmsToAdmin(sendReqVO.getMobile(), null,
                sendReqVO.getTemplateCode(), sendReqVO.getTemplateParams()));
    }

}

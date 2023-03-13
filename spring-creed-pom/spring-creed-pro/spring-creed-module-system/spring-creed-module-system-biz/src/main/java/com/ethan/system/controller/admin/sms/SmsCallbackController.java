package com.ethan.system.controller.admin.sms;

import cn.hutool.core.util.URLUtil;
import com.ethan.common.common.R;
import com.ethan.system.controller.admin.sms.vo.enums.SmsChannelEnum;
import com.ethan.system.service.sms.SmsSendService;
import com.ethan.framework.operatelog.annotations.OperateLog;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ethan.common.common.R.success;

@Tag(name = "管理后台 - 短信回调")
@RestController
@RequestMapping("/system/sms/callback")
public class SmsCallbackController {

    @Resource
    private SmsSendService smsSendService;

    @PostMapping("/yunpian")
    @PermitAll
    @Schema(name = "云片短信的回调", description = "参见 https://www.yunpian.com/official/document/sms/zh_cn/domestic_push_report 文档")
    @Parameter(name = "sms_status", description = "发送状态", required = true, example = "[{具体内容}]")
    @OperateLog(enable = false)
    public String receiveYunpianSmsStatus(@RequestParam("sms_status") String smsStatus) throws Throwable {
        String text = URLUtil.decode(smsStatus); // decode 解码参数，因为它被 encode
        smsSendService.receiveSmsStatus(SmsChannelEnum.YUN_PIAN.getCode(), text);
        return "SUCCESS"; // 约定返回 SUCCESS 为成功
    }

    @PostMapping("/aliyun")
    @PermitAll
    @Schema(name = "阿里云短信的回调", description = "参见 https://help.aliyun.com/document_detail/120998.html 文档")
    @OperateLog(enable = false)
    public R<Boolean> receiveAliyunSmsStatus(HttpServletRequest request) throws Throwable {
        String text = "";//ServletUtil.getBody(request);
        smsSendService.receiveSmsStatus(SmsChannelEnum.ALIYUN.getCode(), text);
        return success(true);
    }

    @PostMapping("/tencent")
    @PermitAll
    @Schema(name = "腾讯云短信的回调", description = "参见 https://cloud.tencent.com/document/product/382/52077 文档")
    @OperateLog(enable = false)
    public R<Boolean> receiveTencentSmsStatus(HttpServletRequest request) throws Throwable {
        String text = "";//ServletUtil.getBody(request);
        smsSendService.receiveSmsStatus(SmsChannelEnum.TENCENT.getCode(), text);
        return success(true);
    }

}

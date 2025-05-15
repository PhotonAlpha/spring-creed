package com.ethan.system.controller.admin.notify;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateRespVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateSendReqVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import com.ethan.system.convert.notify.NotifyTemplateConvert;
import com.ethan.system.dal.entity.notify.NotifyTemplateDO;
import com.ethan.system.service.notify.NotifySendService;
import com.ethan.system.service.notify.NotifyTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static com.ethan.common.common.R.success;


@Tag(name = "管理后台 - 站内信模版")
@RestController
@RequestMapping("/system/notify-template")
@Validated
public class NotifyTemplateController {

    @Resource
    private NotifyTemplateService notifyTemplateService;

    @Resource
    private NotifySendService notifySendService;

    @PostMapping("/create")
    @Operation(summary = "创建站内信模版")
    @PreAuthorize("@ss.hasPermission('system:notify-template:create')")
    public R<Long> createNotifyTemplate(@Valid @RequestBody NotifyTemplateCreateReqVO createReqVO) {
        return success(notifyTemplateService.createNotifyTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新站内信模版")
    @PreAuthorize("@ss.hasPermission('system:notify-template:update')")
    public R<Boolean> updateNotifyTemplate(@Valid @RequestBody NotifyTemplateUpdateReqVO updateReqVO) {
        notifyTemplateService.updateNotifyTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除站内信模版")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:notify-template:delete')")
    public R<Boolean> deleteNotifyTemplate(@RequestParam("id") Long id) {
        notifyTemplateService.deleteNotifyTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得站内信模版")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notify-template:query')")
    public R<NotifyTemplateRespVO> getNotifyTemplate(@RequestParam("id") Long id) {
        NotifyTemplateDO notifyTemplate = notifyTemplateService.getNotifyTemplate(id);
        return success(NotifyTemplateConvert.INSTANCE.convert(notifyTemplate));
    }

    @GetMapping("/page")
    @Operation(summary = "获得站内信模版分页")
    @PreAuthorize("@ss.hasPermission('system:notify-template:query')")
    public R<PageResult<NotifyTemplateRespVO>> getNotifyTemplatePage(@Valid NotifyTemplatePageReqVO pageVO) {
        PageResult<NotifyTemplateDO> pageResult = notifyTemplateService.getNotifyTemplatePage(pageVO);
        return success(NotifyTemplateConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/send-notify")
    @Operation(summary = "发送站内信")
    @PreAuthorize("@ss.hasPermission('system:notify-template:send-notify')")
    public R<Long> sendNotify(@Valid @RequestBody NotifyTemplateSendReqVO sendReqVO) {
        return success(notifySendService.sendSingleNotifyToAdmin(sendReqVO.getUserId(),
                sendReqVO.getTemplateCode(), sendReqVO.getTemplateParams()));
    }

}

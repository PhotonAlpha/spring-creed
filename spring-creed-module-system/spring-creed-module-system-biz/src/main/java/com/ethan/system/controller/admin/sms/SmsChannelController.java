package com.ethan.system.controller.admin.sms;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelRespVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelSimpleRespVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
import com.ethan.system.convert.sms.SmsChannelConvert;
import com.ethan.system.dal.entity.sms.SmsChannelDO;
import com.ethan.system.service.sms.SmsChannelService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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

import java.util.Comparator;
import java.util.List;

import static com.ethan.common.common.R.success;

@Tag(name = "管理后台 - 短信渠道")
@RestController
@RequestMapping("system/sms-channel")
public class SmsChannelController {

    @Resource
    private SmsChannelService smsChannelService;

    @PostMapping("/create")
    @Schema(name = "创建短信渠道")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:create')")
    public R<Long> createSmsChannel(@Valid @RequestBody SmsChannelCreateReqVO createReqVO) {
        return success(smsChannelService.createSmsChannel(createReqVO));
    }

    @PutMapping("/update")
    @Schema(name = "更新短信渠道")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:update')")
    public R<Boolean> updateSmsChannel(@Valid @RequestBody SmsChannelUpdateReqVO updateReqVO) {
        smsChannelService.updateSmsChannel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Schema(name = "删除短信渠道")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-channel:delete')")
    public R<Boolean> deleteSmsChannel(@RequestParam("id") Long id) {
        smsChannelService.deleteSmsChannel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Schema(name = "获得短信渠道")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public R<SmsChannelRespVO> getSmsChannel(@RequestParam("id") Long id) {
        SmsChannelDO smsChannel = smsChannelService.getSmsChannel(id);
        return success(SmsChannelConvert.INSTANCE.convert(smsChannel));
    }

    @GetMapping("/page")
    @Schema(name = "获得短信渠道分页")
    @PreAuthorize("@ss.hasPermission('system:sms-channel:query')")
    public R<PageResult<SmsChannelRespVO>> getSmsChannelPage(@Valid SmsChannelPageReqVO pageVO) {
        PageResult<SmsChannelDO> pageResult = smsChannelService.getSmsChannelPage(pageVO);
        return success(SmsChannelConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-all-simple")
    @Schema(name = "获得短信渠道精简列表", description = "包含被禁用的短信渠道")
    public R<List<SmsChannelSimpleRespVO>> getSimpleSmsChannels() {
        List<SmsChannelDO> list = smsChannelService.getSmsChannelList();
        // 排序后，返回给前端
        list.sort(Comparator.comparing(SmsChannelDO::getId));
        return success(SmsChannelConvert.INSTANCE.convertList03(list));
    }

}

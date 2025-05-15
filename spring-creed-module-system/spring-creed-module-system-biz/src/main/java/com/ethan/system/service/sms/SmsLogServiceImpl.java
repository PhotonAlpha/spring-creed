package com.ethan.system.service.sms;

import com.ethan.common.common.R;
import com.ethan.common.pojo.PageResult;
import com.ethan.system.api.constant.sms.SmsReceiveStatusEnum;
import com.ethan.system.api.constant.sms.SmsSendStatusEnum;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogExportReqVO;
import com.ethan.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.ethan.system.dal.entity.sms.SmsLogDO;
import com.ethan.system.dal.entity.sms.SmsTemplateDO;
import com.ethan.system.dal.repository.sms.SmsLogRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 短信日志 Service 实现类
 *
 * @author zzf
 */
@Slf4j
@Service
public class SmsLogServiceImpl implements SmsLogService {

    @Resource
    private SmsLogRepository smsLogRepository;

    @Override
    public Long createSmsLog(String mobile, Long userId, Integer userType, Boolean isSend,
                             SmsTemplateDO template, String templateContent, Map<String, Object> templateParams) {
        SmsLogDO.SmsLogDOBuilder logBuilder = SmsLogDO.builder();
        // 根据是否要发送，设置状态
        logBuilder.sendStatus(Objects.equals(isSend, true) ? SmsSendStatusEnum.INIT.getStatus()
                : SmsSendStatusEnum.IGNORE.getStatus());
        // 设置手机相关字段
        logBuilder.mobile(mobile).userId(userId).userType(userType);
        // 设置模板相关字段
        logBuilder.templateId(template.getId()).templateCode(template.getCode()).templateType(template.getType());
        logBuilder.templateContent(templateContent).templateParams(templateParams)
                .apiTemplateId(template.getApiTemplateId());
        // 设置渠道相关字段
        logBuilder.channelId(template.getChannelId()).channelCode(template.getChannelCode());
        // 设置接收相关字段
        logBuilder.receiveStatus(SmsReceiveStatusEnum.INIT.getStatus());

        // 插入数据库
        SmsLogDO logDO = logBuilder.build();
        smsLogRepository.save(logDO);
        return logDO.getId();
    }

    @Override
    public void updateSmsSendResult(Long id, Integer sendCode, String sendMsg,
                                    String apiSendCode, String apiSendMsg,
                                    String apiRequestId, String apiSerialNo) {
        SmsSendStatusEnum sendStatus = R.isSuccess(sendCode) ?
                SmsSendStatusEnum.SUCCESS : SmsSendStatusEnum.FAILURE;
        smsLogRepository.save(SmsLogDO.builder().id(id).sendStatus(sendStatus.getStatus())
                .sendTime(new Date()).sendCode(sendCode).sendMsg(sendMsg)
                .apiSendCode(apiSendCode).apiSendMsg(apiSendMsg)
                .apiRequestId(apiRequestId).apiSerialNo(apiSerialNo).build());
    }

    @Override
    public void updateSmsReceiveResult(Long id, Boolean success, Date receiveTime,
                                       String apiReceiveCode, String apiReceiveMsg) {
        SmsReceiveStatusEnum receiveStatus = Objects.equals(success, true) ?
                SmsReceiveStatusEnum.SUCCESS : SmsReceiveStatusEnum.FAILURE;
        smsLogRepository.save(SmsLogDO.builder().id(id).receiveStatus(receiveStatus.getStatus())
                .receiveTime(receiveTime).apiReceiveCode(apiReceiveCode).apiReceiveMsg(apiReceiveMsg).build());
    }

    @Override
    public PageResult<SmsLogDO> getSmsLogPage(SmsLogPageReqVO pageReqVO) {
        // return smsLogMapper.selectPage(pageReqVO);
        return null;
    }

    @Override
    public List<SmsLogDO> getSmsLogList(SmsLogExportReqVO exportReqVO) {
        // return smsLogMapper.selectList(exportReqVO);
        return null;
    }

}

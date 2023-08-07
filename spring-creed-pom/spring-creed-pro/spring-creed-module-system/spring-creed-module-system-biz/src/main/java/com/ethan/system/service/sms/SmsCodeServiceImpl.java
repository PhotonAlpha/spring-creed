package com.ethan.system.service.sms;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.ethan.common.exception.util.ServiceExceptionUtil;
import com.ethan.common.utils.date.DateUtils;
import com.ethan.system.api.constant.sms.SmsSceneEnum;
import com.ethan.system.controller.admin.sms.dto.code.SmsCodeCheckReqDTO;
import com.ethan.system.controller.admin.sms.dto.code.SmsCodeSendReqDTO;
import com.ethan.system.controller.admin.sms.dto.code.SmsCodeUseReqDTO;
import com.ethan.system.dal.entity.sms.SmsCodeDO;
import com.ethan.system.dal.repository.sms.SmsCodeRepository;
import com.ethan.system.framework.sms.SmsCodeProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CODE_EXPIRED;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CODE_NOT_FOUND;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CODE_SEND_TOO_FAST;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CODE_USED;

/**
 * 短信验证码 Service 实现类
 *
 * 
 */
@Service
@Validated
public class SmsCodeServiceImpl implements SmsCodeService {

    @Resource
    private SmsCodeProperties smsCodeProperties;

    @Resource
    private SmsCodeRepository smsCodeRepository;

    @Resource
    private SmsSendService smsSendService;

    @Override
    public void sendSmsCode(SmsCodeSendReqDTO reqDTO) {
        SmsSceneEnum sceneEnum = SmsSceneEnum.getCodeByScene(reqDTO.getScene());
        Assert.notNull(sceneEnum, "验证码场景({}) 查找不到配置", reqDTO.getScene());
        // 创建验证码
        String code = createSmsCode(reqDTO.getMobile(), reqDTO.getScene(), reqDTO.getCreateIp());
        // 发送验证码
        smsSendService.sendSingleSms(reqDTO.getMobile(), null, null,
                sceneEnum.getTemplateCode(), MapUtil.of("code", code));
    }

    private String createSmsCode(String mobile, Integer scene, String ip) {
        // 校验是否可以发送验证码，不用筛选场景
        SmsCodeDO lastSmsCode = smsCodeRepository.findTop1ByMobileAndCodeAndSceneOrderByIdDesc(mobile, null,null);
        if (lastSmsCode != null) {
            if (System.currentTimeMillis() - lastSmsCode.getCreateTime().toEpochSecond()
                    < smsCodeProperties.getSendFrequency().toMillis()) { // 发送过于频繁
                throw ServiceExceptionUtil.exception(SMS_CODE_SEND_TOO_FAST);
            }
            if (DateUtils.isToday(lastSmsCode.getCreateTime()) && // 必须是今天，才能计算超过当天的上限
                    lastSmsCode.getTodayIndex() >= smsCodeProperties.getSendMaximumQuantityPerDay()) { // 超过当天发送的上限。
                throw ServiceExceptionUtil.exception(SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
            }
            // TODO 芋艿：提升，每个 IP 每天可发送数量
            // TODO 芋艿：提升，每个 IP 每小时可发送数量
        }

        // 创建验证码记录
        String code = String.valueOf(randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));
        SmsCodeDO newSmsCode = SmsCodeDO.builder().mobile(mobile).code(code).scene(scene)
                .todayIndex(lastSmsCode != null && DateUtils.isToday(lastSmsCode.getCreateTime()) ? lastSmsCode.getTodayIndex() + 1 : 1)
                .createIp(ip).used(false).build();
        smsCodeRepository.save(newSmsCode);
        return code;
    }

    @Override
    public void useSmsCode(SmsCodeUseReqDTO reqDTO) {
        // 检测验证码是否有效
        SmsCodeDO lastSmsCode = this.checkSmsCode0(reqDTO.getMobile(), reqDTO.getCode(), reqDTO.getScene());
        // 使用验证码
        smsCodeRepository.save(SmsCodeDO.builder().id(lastSmsCode.getId())
                .used(true).usedTime(new Date()).usedIp(reqDTO.getUsedIp()).build());
    }

    @Override
    public void checkSmsCode(SmsCodeCheckReqDTO reqDTO) {
        checkSmsCode0(reqDTO.getMobile(), reqDTO.getCode(), reqDTO.getScene());
    }

    public SmsCodeDO checkSmsCode0(String mobile, String code, Integer scene) {
        // 校验验证码
        SmsCodeDO lastSmsCode = smsCodeRepository.findTop1ByMobileAndCodeAndSceneOrderByIdDesc(mobile,code,scene);
        // 若验证码不存在，抛出异常
        if (lastSmsCode == null) {
            throw ServiceExceptionUtil.exception(SMS_CODE_NOT_FOUND);
        }
        // 超过时间
        if (System.currentTimeMillis() - lastSmsCode.getCreateTime().toEpochSecond()
                >= smsCodeProperties.getExpireTimes().toMillis()) { // 验证码已过期
            throw ServiceExceptionUtil.exception(SMS_CODE_EXPIRED);
        }
        // 判断验证码是否已被使用
        if (Boolean.TRUE.equals(lastSmsCode.getUsed())) {
            throw ServiceExceptionUtil.exception(SMS_CODE_USED);
        }
        return lastSmsCode;
    }

}

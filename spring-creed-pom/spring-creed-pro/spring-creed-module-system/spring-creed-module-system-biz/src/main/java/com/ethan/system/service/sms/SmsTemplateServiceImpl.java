package com.ethan.system.service.sms;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateCreateReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateExportReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import com.ethan.system.controller.admin.sms.vo.template.SmsTemplateUpdateReqVO;
import com.ethan.system.convert.sms.SmsTemplateConvert;
import com.ethan.system.dal.entity.sms.SmsChannelDO;
import com.ethan.system.dal.entity.sms.SmsTemplateDO;
import com.ethan.system.dal.repository.sms.SmsTemplateRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CHANNEL_DISABLE;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_TEMPLATE_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_TEMPLATE_NOT_EXISTS;

/**
 * 短信模板 Service 实现类
 *
 * @author zzf
 * @date 2021/1/25 9:25
 */
@Service
@Slf4j
public class SmsTemplateServiceImpl implements SmsTemplateService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private SmsTemplateRepository smsTemplateRepository;

    @Resource
    private SmsChannelService smsChannelService;

    // @Resource
    // private SmsClientFactory smsClientFactory;

    // @Resource
    // private SmsProducer smsProducer;

    /**
     * 短信模板缓存
     * key：短信模板编码 {@link SmsTemplateDO#getCode()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    private volatile Map<String, SmsTemplateDO> smsTemplateCache;
    /**
     * 缓存短信模板的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    private volatile Instant maxUpdateTime;

    @Override
    @PostConstruct
    public void initLocalCache() {
        // 获取短信模板列表，如果有更新
        List<SmsTemplateDO> smsTemplateList = this.loadSmsTemplateIfUpdate(maxUpdateTime);
        if (CollUtil.isEmpty(smsTemplateList)) {
            return;
        }

        // 写入缓存
        smsTemplateCache = CollUtils.convertMap(smsTemplateList, SmsTemplateDO::getCode);
        maxUpdateTime = CollUtils.getMaxValue(smsTemplateList, SmsTemplateDO::getUpdateTime);
        log.info("[initLocalCache][初始化 SmsTemplate 数量为 {}]", smsTemplateList.size());
    }

    /**
     * 如果短信模板发生变化，从数据库中获取最新的全量短信模板。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前短信模板的最大更新时间
     * @return 短信模板列表
     */
    private List<SmsTemplateDO> loadSmsTemplateIfUpdate(Instant maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadSmsTemplateIfUpdate][首次加载全量短信模板]");
        } else { // 判断数据库中是否有更新的短信模板
            if (smsTemplateRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadSmsTemplateIfUpdate][增量加载全量短信模板]");
        }
        // 第二步，如果有更新，则从数据库加载所有短信模板
        return smsTemplateRepository.findAll();
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        initLocalCache();
    }

    @Override
    public SmsTemplateDO getSmsTemplateByCodeFromCache(String code) {
        return smsTemplateCache.get(code);
    }

    @Override
    public String formatSmsTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    @Override
    public SmsTemplateDO getSmsTemplateByCode(String code) {
        return smsTemplateRepository.findByCode(code);
    }

    @VisibleForTesting
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @Override
    public Long createSmsTemplate(SmsTemplateCreateReqVO createReqVO) {
        // 校验短信渠道
        SmsChannelDO channelDO = checkSmsChannel(createReqVO.getChannelId());
        // 校验短信编码是否重复
        checkSmsTemplateCodeDuplicate(null, createReqVO.getCode());
        // 校验短信模板
        checkApiTemplate(createReqVO.getChannelId(), createReqVO.getApiTemplateId());

        // 插入
        SmsTemplateDO template = SmsTemplateConvert.INSTANCE.convert(createReqVO);
        template.setParams(parseTemplateContentParams(template.getContent()));
        template.setChannelCode(channelDO.getCode());
        smsTemplateRepository.save(template);
        // 发送刷新消息
        // smsProducer.sendSmsTemplateRefreshMessage();
        // 返回
        return template.getId();
    }

    @Override
    public void updateSmsTemplate(SmsTemplateUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateSmsTemplateExists(updateReqVO.getId());
        // 校验短信渠道
        SmsChannelDO channelDO = checkSmsChannel(updateReqVO.getChannelId());
        // 校验短信编码是否重复
        checkSmsTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());
        // 校验短信模板
        checkApiTemplate(updateReqVO.getChannelId(), updateReqVO.getApiTemplateId());

        // 更新
        SmsTemplateDO updateObj = SmsTemplateConvert.INSTANCE.convert(updateReqVO);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        updateObj.setChannelCode(channelDO.getCode());
        smsTemplateRepository.save(updateObj);
        // 发送刷新消息
        // smsProducer.sendSmsTemplateRefreshMessage();
    }

    @Override
    public void deleteSmsTemplate(Long id) {
        // 校验存在
        this.validateSmsTemplateExists(id);
        // 更新
        smsTemplateRepository.deleteById(id);
        // 发送刷新消息
        // smsProducer.sendSmsTemplateRefreshMessage();
    }

    private void validateSmsTemplateExists(Long id) {
        if (smsTemplateRepository.findById(id) == null) {
            throw exception(SMS_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public SmsTemplateDO getSmsTemplate(Long id) {
        return smsTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public List<SmsTemplateDO> getSmsTemplateList(Collection<Long> ids) {
        return smsTemplateRepository.findAllById(ids);
    }

    @Override
    public PageResult<SmsTemplateDO> getSmsTemplatePage(SmsTemplatePageReqVO pageReqVO) {
        Page<SmsTemplateDO> page = smsTemplateRepository.findAll(PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    @Override
    public List<SmsTemplateDO> getSmsTemplateList(SmsTemplateExportReqVO exportReqVO) {
        return smsTemplateRepository.findAll(getSmsTemplateListSpecification(exportReqVO));
    }

    private static Specification<SmsTemplateDO> getSmsTemplateListSpecification(SmsTemplateExportReqVO reqVO) {
        return (Specification<SmsTemplateDO>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (Objects.nonNull(reqVO.getType())) {
                predicateList.add(cb.equal(root.get("type"), reqVO.getType()));
            }
            if (Objects.nonNull(reqVO.getStatus())) {
                predicateList.add(cb.equal(root.get("status"), reqVO.getStatus()));
            }
            if (Objects.nonNull(reqVO.getChannelId())) {
                predicateList.add(cb.equal(root.get("channel_id"), reqVO.getChannelId()));
            }
            if (Objects.nonNull(reqVO.getCode())) {
                predicateList.add(cb.like(root.get("code"), "%" + reqVO.getCode() + "%"));
            }
            if (Objects.nonNull(reqVO.getContent())) {
                predicateList.add(cb.like(root.get("content"), "%" + reqVO.getContent() + "%"));
            }
            if (Objects.nonNull(reqVO.getApiTemplateId())) {
                predicateList.add(cb.like(root.get("api_template_id"), "%" + reqVO.getApiTemplateId() + "%"));
            }
            cb.desc(root.get("id"));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    @Override
    public Long countByChannelId(Long channelId) {
        return smsTemplateRepository.countByChannelId(channelId);
    }

    @VisibleForTesting
    public SmsChannelDO checkSmsChannel(Long channelId) {
        SmsChannelDO channelDO = smsChannelService.getSmsChannel(channelId);
        if (channelDO == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        if (!Objects.equals(channelDO.getStatus(), CommonStatusEnum.ENABLE.getStatus())) {
            throw exception(SMS_CHANNEL_DISABLE);
        }
        return channelDO;
    }

    @VisibleForTesting
    public void checkSmsTemplateCodeDuplicate(Long id, String code) {
        SmsTemplateDO template = smsTemplateRepository.findByCode(code);
        if (template == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(SMS_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(SMS_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 校验 API 短信平台的模板是否有效
     *
     * @param channelId 渠道编号
     * @param apiTemplateId API 模板编号
     */
    @VisibleForTesting
    public void checkApiTemplate(Long channelId, String apiTemplateId) {
        // 获得短信模板
        // SmsClient smsClient = smsClientFactory.getSmsClient(channelId);
        // Assert.notNull(smsClient, String.format("短信客户端(%d) 不存在", channelId));
        // SmsCommonResult<SmsTemplateRespDTO> templateResult = smsClient.getSmsTemplate(apiTemplateId);
        // 校验短信模板是否正确
        // templateResult.checkError();
    }

}

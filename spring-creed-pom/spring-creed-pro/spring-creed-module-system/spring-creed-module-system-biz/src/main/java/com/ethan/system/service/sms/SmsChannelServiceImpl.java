package com.ethan.system.service.sms;

import cn.hutool.core.collection.CollUtil;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.controller.admin.sms.property.SmsChannelProperties;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.ethan.system.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
import com.ethan.system.convert.sms.SmsChannelConvert;
import com.ethan.system.dal.entity.sms.SmsChannelDO;
import com.ethan.system.dal.repository.sms.SmsChannelRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN;
import static com.ethan.system.constant.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;

/**
 * 短信渠道Service实现类
 *
 * @author zzf
 * @date 2021/1/25 9:25
 */
@Service
@Slf4j
public class SmsChannelServiceImpl implements SmsChannelService {

    /**
     * 定时执行 {@link #schedulePeriodicRefresh()} 的周期
     * 因为已经通过 Redis Pub/Sub 机制，所以频率不需要高
     */
    private static final long SCHEDULER_PERIOD = 5 * 60 * 1000L;

    /**
     * 缓存菜单的最大更新时间，用于后续的增量轮询，判断是否有更新
     */
    private volatile Instant maxUpdateTime;

    // @Resource
    // private SmsClientFactory smsClientFactory;

    @Resource
    private SmsChannelRepository smsChannelRepository;

    @Resource
    private SmsTemplateService smsTemplateService;

    // @Resource
    // private SmsProducer smsProducer;

    @Override
    @PostConstruct
    public void initSmsClients() {
        // 获取短信渠道，如果有更新
        List<SmsChannelDO> smsChannels = this.loadSmsChannelIfUpdate(maxUpdateTime);
        if (CollUtil.isEmpty(smsChannels)) {
            return;
        }

        // 创建或更新短信 Client
        List<SmsChannelProperties> propertiesList = SmsChannelConvert.INSTANCE.convertList02(smsChannels);
        // propertiesList.forEach(properties -> smsClientFactory.createOrUpdateSmsClient(properties));

        // 写入缓存
        maxUpdateTime = CollUtils.getMaxValue(smsChannels, SmsChannelDO::getUpdateTime);
        log.info("[initSmsClients][初始化 SmsChannel 数量为 {}]", smsChannels.size());
    }

    @Scheduled(fixedDelay = SCHEDULER_PERIOD, initialDelay = SCHEDULER_PERIOD)
    public void schedulePeriodicRefresh() {
        initSmsClients();
    }

    /**
     * 如果短信渠道发生变化，从数据库中获取最新的全量短信渠道。
     * 如果未发生变化，则返回空
     *
     * @param maxUpdateTime 当前短信渠道的最大更新时间
     * @return 短信渠道列表
     */
    private List<SmsChannelDO> loadSmsChannelIfUpdate(Instant maxUpdateTime) {
        // 第一步，判断是否要更新。
        if (maxUpdateTime == null) { // 如果更新时间为空，说明 DB 一定有新数据
            log.info("[loadSmsChannelIfUpdate][首次加载全量短信渠道]");
        } else { // 判断数据库中是否有更新的短信渠道
            if (smsChannelRepository.countByUpdateTimeGreaterThan(maxUpdateTime) == 0) {
                return null;
            }
            log.info("[loadSmsChannelIfUpdate][增量加载全量短信渠道]");
        }
        // 第二步，如果有更新，则从数据库加载所有短信渠道
        return smsChannelRepository.findAll();
    }

    @Override
    public Long createSmsChannel(SmsChannelCreateReqVO createReqVO) {
        // 插入
        SmsChannelDO smsChannel = SmsChannelConvert.INSTANCE.convert(createReqVO);
        smsChannelRepository.save(smsChannel);
        // 发送刷新消息
        // smsProducer.sendSmsChannelRefreshMessage();
        // 返回
        return smsChannel.getId();
    }

    @Override
    public void updateSmsChannel(SmsChannelUpdateReqVO updateReqVO) {
        // 校验存在
        this.validateSmsChannelExists(updateReqVO.getId());
        // 更新
        SmsChannelDO updateObj = SmsChannelConvert.INSTANCE.convert(updateReqVO);
        smsChannelRepository.save(updateObj);
        // 发送刷新消息
        // smsProducer.sendSmsChannelRefreshMessage();
    }

    @Override
    public void deleteSmsChannel(Long id) {
        // 校验存在
        this.validateSmsChannelExists(id);
        // 校验是否有字典数据
        if (smsTemplateService.countByChannelId(id) > 0) {
            throw exception(SMS_CHANNEL_HAS_CHILDREN);
        }
        // 删除
        smsChannelRepository.deleteById(id);
        // 发送刷新消息
        // smsProducer.sendSmsChannelRefreshMessage();
    }

    private void validateSmsChannelExists(Long id) {
        if (smsChannelRepository.findById(id) == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
    }

    @Override
    public SmsChannelDO getSmsChannel(Long id) {
        return smsChannelRepository.findById(id).orElse(null);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList(Collection<Long> ids) {
        return smsChannelRepository.findAllById(ids);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList() {
        return smsChannelRepository.findAll();
    }

    @Override
    public PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO) {
        Page<SmsChannelDO> page = smsChannelRepository.findAll(PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()));
        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

}

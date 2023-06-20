package com.ethan.system.service.notify;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ethan.common.pojo.PageResult;
import com.ethan.common.utils.collection.CollUtils;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.ethan.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import com.ethan.system.convert.notify.NotifyTemplateConvert;
import com.ethan.system.dal.entity.notify.NotifyTemplateDO;
import com.ethan.system.dal.repository.notify.NotifyTemplateRepository;
import com.ethan.system.mq.producer.notify.NotifyProducer;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.NOTIFY_TEMPLATE_CODE_DUPLICATE;
import static com.ethan.system.constant.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;


/**
 * 站内信模版 Service 实现类
 *
 * @author xrcoder
 */
@Service
@Validated
@Slf4j
public class NotifyTemplateServiceImpl implements NotifyTemplateService {

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private NotifyTemplateRepository notifyTemplateRepository;

    @Resource
    private NotifyProducer notifyProducer;

    /**
     * 站内信模板缓存
     * key：站内信模板编码 {@link NotifyTemplateDO#getCode()}
     * <p>
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    private volatile Map<String, NotifyTemplateDO> notifyTemplateCache;

    /**
     * 初始化站内信模板的本地缓存
     */
    @Override
    @PostConstruct
    public void initLocalCache() {
        // 第一步：查询数据
        List<NotifyTemplateDO> templates = notifyTemplateRepository.findAll();
        log.info("[initLocalCache][缓存站内信模版，数量为:{}]", templates.size());

        // 第二步：构建缓存
        notifyTemplateCache = CollUtils.convertMap(templates, NotifyTemplateDO::getCode);
    }

    @Override
    public NotifyTemplateDO getNotifyTemplateByCodeFromCache(String code) {
        return notifyTemplateCache.get(code);
    }

    @Override
    public Long createNotifyTemplate(NotifyTemplateCreateReqVO createReqVO) {
        // 校验站内信编码是否重复
        validateNotifyTemplateCodeDuplicate(null, createReqVO.getCode());

        // 插入
        NotifyTemplateDO notifyTemplate = NotifyTemplateConvert.INSTANCE.convert(createReqVO);
        notifyTemplate.setParams(parseTemplateContentParams(notifyTemplate.getContent()));
        notifyTemplateRepository.save(notifyTemplate);

        // 发送刷新消息
        notifyProducer.sendNotifyTemplateRefreshMessage();
        return notifyTemplate.getId();
    }

    @Override
    public void updateNotifyTemplate(NotifyTemplateUpdateReqVO updateReqVO) {
        // 校验存在
        validateNotifyTemplateExists(updateReqVO.getId());
        // 校验站内信编码是否重复
        validateNotifyTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());

        // 更新
        NotifyTemplateDO updateObj = NotifyTemplateConvert.INSTANCE.convert(updateReqVO);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        notifyTemplateRepository.save(updateObj);

        // 发送刷新消息
        notifyProducer.sendNotifyTemplateRefreshMessage();
    }

    @VisibleForTesting
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @Override
    public void deleteNotifyTemplate(Long id) {
        // 校验存在
        validateNotifyTemplateExists(id);
        // 删除
        notifyTemplateRepository.deleteById(id);
        // 发送刷新消息
        notifyProducer.sendNotifyTemplateRefreshMessage();
    }

    private void validateNotifyTemplateExists(Long id) {
        if (notifyTemplateRepository.findById(id) == null) {
            throw exception(NOTIFY_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public NotifyTemplateDO getNotifyTemplate(Long id) {
        return notifyTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public PageResult<NotifyTemplateDO> getNotifyTemplatePage(NotifyTemplatePageReqVO pageReqVO) {
        Page<NotifyTemplateDO> page = notifyTemplateRepository.findByPage(pageReqVO);
        return new PageResult<>(page.toList(), page.getTotalElements());
    }

    @VisibleForTesting
    public void validateNotifyTemplateCodeDuplicate(Long id, String code) {
        NotifyTemplateDO template = notifyTemplateRepository.findByCode(code);
        if (template == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 格式化站内信内容
     *
     * @param content 站内信模板的内容
     * @param params  站内信内容的参数
     * @return 格式化后的内容
     */
    @Override
    public String formatNotifyTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }
}

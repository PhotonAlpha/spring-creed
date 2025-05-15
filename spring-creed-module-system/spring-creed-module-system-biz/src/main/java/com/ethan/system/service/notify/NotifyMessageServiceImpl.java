package com.ethan.system.service.notify;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.ethan.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.ethan.system.dal.entity.notify.NotifyMessageDO;
import com.ethan.system.dal.entity.notify.NotifyTemplateDO;
import com.ethan.system.dal.repository.notify.NotifyMessageRepository;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 站内信 Service 实现类
 *
 * @author xrcoder
 */
@Service
@Validated
public class NotifyMessageServiceImpl implements NotifyMessageService {

    @Resource
    private NotifyMessageRepository notifyMessageRepository;

    @Override
    public Long createNotifyMessage(Long userId, Integer userType,
                                    NotifyTemplateDO template, String templateContent, Map<String, Object> templateParams) {
        NotifyMessageDO message = new NotifyMessageDO().setUserId(userId).setUserType(userType)
                .setTemplateId(template.getId()).setTemplateCode(template.getCode())
                .setTemplateType(template.getType()).setTemplateNickname(template.getNickname())
                .setTemplateContent(templateContent).setTemplateParams(templateParams).setReadStatus(false);
        notifyMessageRepository.save(message);
        return message.getId();
    }

    @Override
    public PageResult<NotifyMessageDO> getNotifyMessagePage(NotifyMessagePageReqVO pageReqVO) {
        Page<NotifyMessageDO> page = notifyMessageRepository.findByPage(pageReqVO);
        return new PageResult<>(page.toList(), page.getTotalElements());
    }

    @Override
    public PageResult<NotifyMessageDO> getMyMyNotifyMessagePage(NotifyMessageMyPageReqVO pageReqVO, Long userId, Integer userType) {
        Page<NotifyMessageDO> page =  notifyMessageRepository.findByPage(pageReqVO, userId, userType);
        return new PageResult<>(page.toList(), page.getTotalElements());
    }

    @Override
    public NotifyMessageDO getNotifyMessage(Long id) {
        return notifyMessageRepository.findById(id).orElse(null);
    }

    @Override
    public List<NotifyMessageDO> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size) {
        return notifyMessageRepository.findByUnreadListByUserIdAndUserType(userId, userType, size);
    }

    @Override
    public Long getUnreadNotifyMessageCount(Long userId, Integer userType) {
        return notifyMessageRepository.countByUnreadCountByUserIdAndUserType(userId, userType);
    }

    @Override
    public int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType) {
        return notifyMessageRepository.updateListRead(ids, userId, userType).size();
    }

    @Override
    public int updateAllNotifyMessageRead(Long userId, Integer userType) {
        return notifyMessageRepository.updateListRead(userId, userType).size();
    }

}

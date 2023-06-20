package com.ethan.system.service.notice;

import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.notice.vo.NoticeCreateReqVO;
import com.ethan.system.controller.admin.notice.vo.NoticePageReqVO;
import com.ethan.system.controller.admin.notice.vo.NoticeUpdateReqVO;
import com.ethan.system.convert.notice.NoticeConvert;
import com.ethan.system.dal.entity.notice.NoticeDO;
import com.ethan.system.dal.repository.notice.NoticeRepository;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static com.ethan.common.exception.util.ServiceExceptionUtil.exception;
import static com.ethan.system.constant.ErrorCodeConstants.NOTICE_NOT_FOUND;

/**
 * 通知公告 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeRepository noticeRepository;

    @Override
    public Long createNotice(NoticeCreateReqVO reqVO) {
        NoticeDO notice = NoticeConvert.INSTANCE.convert(reqVO);
        noticeRepository.save(notice);
        return notice.getId();
    }

    @Override
    public void updateNotice(NoticeUpdateReqVO reqVO) {
        // 校验是否存在
        validateNoticeExists(reqVO.getId());
        // 更新通知公告
        NoticeDO updateObj = NoticeConvert.INSTANCE.convert(reqVO);
        noticeRepository.save(updateObj);
    }

    @Override
    public void deleteNotice(Long id) {
        // 校验是否存在
        validateNoticeExists(id);
        // 删除通知公告
        noticeRepository.deleteById(id);
    }

    @Override
    public PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO) {
        Page<NoticeDO> page = noticeRepository.findByPage(reqVO);
        return new PageResult(page.toList(), page.getTotalElements());
    }

    @Override
    public NoticeDO getNotice(Long id) {
        return noticeRepository.findById(id).orElse(null);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        NoticeDO notice = noticeRepository.findById(id).orElse(null);
        if (notice == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }

}

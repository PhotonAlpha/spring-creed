package com.ethan.system.service.notice;


import com.ethan.common.pojo.PageResult;
import com.ethan.system.controller.admin.notice.vo.NoticeCreateReqVO;
import com.ethan.system.controller.admin.notice.vo.NoticePageReqVO;
import com.ethan.system.controller.admin.notice.vo.NoticeUpdateReqVO;
import com.ethan.system.dal.entity.notice.NoticeDO;

/**
 * 通知公告 Service 接口
 */
public interface NoticeService {

    /**
     * 创建岗位公告公告
     *
     * @param reqVO 岗位公告公告信息
     * @return 岗位公告公告编号
     */
    Long createNotice(NoticeCreateReqVO reqVO);

    /**
     * 更新岗位公告公告
     *
     * @param reqVO 岗位公告公告信息
     */
    void updateNotice(NoticeUpdateReqVO reqVO);

    /**
     * 删除岗位公告公告信息
     *
     * @param id 岗位公告公告编号
     */
    void deleteNotice(Long id);

    /**
     * 获得岗位公告公告分页列表
     *
     * @param reqVO 分页条件
     * @return 部门分页列表
     */
    PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO);

    /**
     * 获得岗位公告公告信息
     *
     * @param id 岗位公告公告编号
     * @return 岗位公告公告信息
     */
    NoticeDO getNotice(Long id);

}

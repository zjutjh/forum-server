package org.jh.forum.api.service;

import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;

/**
 * 公告服务接口
 * RPC远程调用接口
 */
public interface AnnouncementService {

    /**
     * 创建公告
     * @param request 创建公告请求
     * @return 创建结果
     */
    AnnouncementOperationResponse createAnnouncement(CreateAnnouncementRequest request);

    /**
     * 根据ID查询公告详情
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    AnnouncementDetailsResponse getAnnouncementById(Integer id);

    /**
     * 查询公告列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    ListAnnouncementResponse listAnnouncements(ListAnnouncementRequest request);

    /**
     * 编辑公告
     * 
     * @param id 公告ID
     * @param request 编辑公告请求
     * @return 编辑结果
     */
    AnnouncementOperationResponse editAnnouncement(Integer id, EditAnnouncementRequest request);

    /**
     * 删除公告
     * 
     * @param id 公告ID
     * @return 删除结果
     */
    AnnouncementOperationResponse deleteAnnouncement(Integer id);    /**
     * 置顶/取消置顶公告
     * 
     * @param id 公告ID
     * @param isSticky true表示置顶，false表示取消置顶
     * @return 操作结果
     */
    AnnouncementOperationResponse stickyAnnouncement(Integer id, Boolean isSticky);    /**
     * 管理员查询公告列表
     * 支持复杂筛选和排序
     * 
     * @param request 管理员查询请求
     * @return 分页结果
     */
    ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request);
}
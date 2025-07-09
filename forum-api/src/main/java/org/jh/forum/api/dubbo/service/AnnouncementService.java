package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.AnnouncementDetailResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;
import org.jh.forum.common.dto.response.ListAnnouncementMinorResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.dto.response.ListAnnouncementTinyResponse;

/**
 * 公告服务接口
 * RPC远程调用接口
 */
public interface AnnouncementService {

    /**
     * 创建公告
     * 
     * @param request 创建公告请求
     * @return 创建结果
     */
    AnnouncementOperationResponse createAnnouncement(CreateAnnouncementRequest request);

    /**
     * 编辑公告
     * 
     * @param id      公告ID
     * @param request 编辑公告请求
     * @return 编辑结果
     */
    AnnouncementOperationResponse editAnnouncement(Long id, EditAnnouncementRequest request);

    /**
     * 删除公告
     * 
     * @param id 公告ID
     * @return 删除结果
     */
    AnnouncementOperationResponse deleteAnnouncement(Long id);

    /**
     * 置顶/取消置顶公告
     * 
     * @param id       公告ID
     * @param isSticky true表示置顶，false表示取消置顶
     * @return 操作结果
     */
    AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean isSticky);

    /**
     * 根据ID查询公告详情
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    AnnouncementDetailResponse getAnnouncementById(Long id);

    /**
     * 根据ID查询公告详情（用户版本）
     * 返回简化的公告信息，不包含管理员才需要的字段
     * 
     * @param id 公告ID
     * @return 公告简化详情
     */
    AnnouncementTinyDetailsResponse getAnnouncementTinyDetailsById(Long id);

    /**
     * 用户查询公告列表
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    ListAnnouncementTinyResponse userListAnnouncements(UserQueryAnnouncementRequest request);

    /**
     * 管理员查询公告列表
     * 支持复杂筛选和排序
     * 
     * @param request 管理员查询请求
     * @return 分页结果
     */
    ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request);

    /**
     * 获取置顶公告
     * @return 置顶公告列表
     */
    ListAnnouncementMinorResponse getTopAnnouncements();

    /**
     * 辅助方法-判断权限
     * 
     * @param announcementId 公告ID
     * @param userId 用户ID
     * @param isSuperAdmin 是否为超级管理员
     * @return 是否为创建者(boolean)
     */
    boolean checkPermission(Long announcementId, Long userId, boolean isSuperAdmin);
}
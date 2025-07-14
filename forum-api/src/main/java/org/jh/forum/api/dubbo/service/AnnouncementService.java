package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.*;

/**
 * 公告服务接口
 *
 * @author SituChengxiang(SK)
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
     * @param request      编辑公告请求
     * @param currentUid   当前用户ID
     * @param isSuperAdmin 是否是超级管理员
     * @return 编辑结果
     */
    AnnouncementOperationResponse editAnnouncement(EditAnnouncementRequest request, Long currentUid,
                                                   boolean isSuperAdmin);

    /**
     * 删除公告
     *
     * @param id           公告ID
     * @param currentUid   当前用户ID
     * @param isSuperAdmin 是否是超级管理员
     * @return 删除结果
     */
    AnnouncementOperationResponse deleteAnnouncement(Long id, Long currentUid, boolean isSuperAdmin);

    /**
     * 置顶/取消置顶公告
     *
     * @param id           公告ID
     * @param sticky       true表示置顶，false表示取消置顶
     * @param currentUid   当前用户ID
     * @param isSuperAdmin 是否是超级管理员
     * @return 操作结果
     */
    AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean sticky, Long currentUid,
                                                     boolean isSuperAdmin);

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
    BaseListResponse<ListAnnouncementTinyItemResponse> userListAnnouncements(UserQueryAnnouncementRequest request);

    /**
     * 管理员查询公告列表
     * 支持复杂筛选和排序
     *
     * @param request 管理员查询请求
     * @return 分页结果
     */
    BaseListResponse<ListAnnouncementItemResponse> adminQueryAnnouncements(AdminQueryAnnouncementRequest request);

    /**
     * 获取置顶公告
     *
     * @return 置顶公告列表
     */
    ListAnnouncementMinorResponse getTopAnnouncements();
}
package org.jh.forum.api.dubbo.service;

import org.jh.forum.common.dto.request.*;
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
     */
    void createAnnouncement(CreateAnnouncementRequest request);

    /**
     * 编辑公告
     *
     * @param request 编辑公告请求
     */
    void editAnnouncement(EditAnnouncementRequest request);

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void deleteAnnouncement(Long id);

    /**
     * 置顶/取消置顶公告
     *
     * @param request 请求体
     */
    void stickyAnnouncement(StickyAnnouncementRequest request);

    /**
     * 根据ID查询公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    GetAdminAnnouncementDetailResponse getAdminAnnouncementDetail(Long id);

    /**
     * 根据ID查询公告详情（用户版本）
     * 返回简化的公告信息，不包含管理员才需要的字段
     *
     * @param id 公告ID
     * @return 公告简化详情
     */
    GetAnnouncementDetailResponse getAnnouncementDetail(Long id);

    /**
     * 用户查询公告列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    BaseListResponse<GetAnnouncementListElement> userListAnnouncements(GetAnnouncementListRequest request);

    /**
     * 管理员查询公告列表
     * 支持复杂筛选和排序
     *
     * @param request 管理员查询请求
     * @return 分页结果
     */
    BaseListResponse<GetAdminAnnouncementListElement> adminQueryAnnouncements(GetAdminAnnouncementListRequest request);

    /**
     * 获取置顶公告
     *
     * @return 置顶公告列表
     */
    StickyAnnouncementList getTopAnnouncements();
}
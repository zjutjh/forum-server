package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.AnnouncementService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.manager.AnnouncementManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 公告服务实现类
 *
 * @author SituChengxiang (SK)
 */
@Slf4j
@Service
@DubboService
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementManager announcementManager;

    /**
     * 创建公告
     */
    @Override
    public void createAnnouncement(CreateAnnouncementRequest request) {
        announcementManager.createAnnouncement(request);
    }

    /**
     * 编辑公告
     */
    @Override
    public void editAnnouncement(EditAnnouncementRequest request) {
        if (!announcementManager.hasPermission(request.getId(), StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        announcementManager.editAnnouncement(request);
    }

    /**
     * 置顶/取消置顶公告
     */
    @Override
    public void stickyAnnouncement(StickyAnnouncementRequest request) {
        if (!announcementManager.hasPermission(request.getId(), StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        announcementManager.stickyAnnouncement(request.getId(), request.getSticky());
    }

    /**
     * 删除公告
     */
    @Override
    public void deleteAnnouncement(Long id) {
        if (!announcementManager.hasPermission(id, StpUtil.getLoginIdAsLong())) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        announcementManager.deleteAnnouncement(id);
    }

    /**
     * 根据ID查询公告详情 (管理员)
     */
    @Override
    public GetAdminAnnouncementDetailResponse getAdminAnnouncementDetail(Long id) {
        return announcementManager.getAdminAnnouncementDetail(id);
    }

    /**
     * 查询公告列表(用户版本)
     */
    @Override
    public BaseListResponse<GetAnnouncementListElement> userListAnnouncements(GetAnnouncementListRequest request) {
        return announcementManager.userListAnnouncements(
                request.getPage(),
                request.getPageSize(),
                request.getType()
        );
    }

    /**
     * 管理员查询公告列表
     */
    @Override
    public BaseListResponse<GetAdminAnnouncementListElement> adminQueryAnnouncements(GetAdminAnnouncementListRequest request) {
        return announcementManager.adminQueryAnnouncements(
                request.getPage(),
                request.getPageSize(),
                request.getType(),
                request.getStatus(),
                request.getOrder(),
                request.getKeyword()
        );
    }

    @Override
    public StickyAnnouncementList getTopAnnouncements() {
        List<Announcement> list = announcementManager.getTopAnnouncements();

        List<StickyAnnouncementList.StickyAnnouncementElement> topAnnouncements = list.stream()
                .map(announcement -> new StickyAnnouncementList.StickyAnnouncementElement(
                        announcement.getId(),
                        announcement.getTitle(),
                        announcement.getSticky()))
                .toList();

        return new StickyAnnouncementList(topAnnouncements);
    }
}

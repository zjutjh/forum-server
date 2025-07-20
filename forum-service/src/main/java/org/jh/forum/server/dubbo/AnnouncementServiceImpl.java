package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.AnnouncementService;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;
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
    private static final String ALL = "all";

    @Resource
    private AnnouncementManager announcementManager;

    /**
     * 创建公告
     */
    @Override
    public void createAnnouncement(CreateAnnouncementRequest request) {
        if (!StpUtil.hasRole("super_admin") && !StpUtil.hasRole("admin")) {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
        announcementManager.createAnnouncement(request);
    }

    /**
     * 编辑公告
     */
    @Override
    public void editAnnouncement(EditAnnouncementRequest request) {
        if (announcementManager.hasPermission(request.getId(), StpUtil.getLoginIdAsLong())) {
            announcementManager.editAnnouncement(request);
        } else {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
    }

    /**
     * 置顶/取消置顶公告
     */
    @Override
    public void stickyAnnouncement(StickyAnnouncementRequest request) {
        if (announcementManager.hasPermission(request.getId(), StpUtil.getLoginIdAsLong())) {
            announcementManager.stickyAnnouncement(request.getId(), request.getSticky());
        } else {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
    }

    /**
     * 删除公告
     */
    @Override
    public void deleteAnnouncement(Long id) {
        if (announcementManager.hasPermission(id, StpUtil.getLoginIdAsLong())) {
            announcementManager.deleteAnnouncement(id);
        } else {
            throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
        }
    }

    /**
     * 根据ID查询公告详情 (管理员)
     */
    @Override
    public GetAdminAnnouncementDetailResponse getAdminAnnouncementDetail(Long id) {
        return announcementManager.getAdminAnnouncementDetail(id);
    }

    /**
     * 根据ID查询公告详情 (用户版本)
     */
    @Override
    public GetAnnouncementDetailResponse getAnnouncementDetail(Long id) {
        return announcementManager.getAnnouncementDetail(id);
    }

    /**
     * 查询公告列表(用户版本)
     */
    @Override
    public BaseListResponse<GetAnnouncementListElement> userListAnnouncements(GetAnnouncementListRequest request) {
        AnnouncementTypeEnum type = null;
        if (!request.getType().equals(ALL)) {
            type = EnumUtil.getBy(AnnouncementTypeEnum::getValue, request.getType());
        }
        return announcementManager.userListAnnouncements(request.getPage(), request.getPageSize(), type);
    }

    /**
     * 管理员查询公告列表
     */
    @Override
    public BaseListResponse<GetAdminAnnouncementListElement> adminQueryAnnouncements(GetAdminAnnouncementListRequest request) {
        AnnouncementTypeEnum type = null;
        if (!request.getType().equals(ALL)) {
            type = EnumUtil.getBy(AnnouncementTypeEnum::getValue, request.getType());
        }
        AnnouncementStatusEnum status = null;
        if (!request.getStatus().equals(ALL)) {
            status = EnumUtil.getBy(AnnouncementStatusEnum::getValue, request.getStatus());
        }
        return announcementManager.adminQueryAnnouncements(
                request.getPage(),
                request.getPageSize(),
                type,
                status,
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
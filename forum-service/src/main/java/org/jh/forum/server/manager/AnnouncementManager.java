package org.jh.forum.server.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.constants.UserTypeEnum;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetAdminAnnouncementDetailResponse;
import org.jh.forum.common.dto.response.GetAdminAnnouncementListElement;
import org.jh.forum.common.dto.response.GetAnnouncementListElement;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.entity.User;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AnnouncementMapper;
import org.jh.forum.server.mapper.UserMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务管理层
 * 负责处理公告相关的业务逻辑
 *
 * @author SituChengxiang
 */
@Slf4j
@Component
public class AnnouncementManager {

    private static final int DEFAULT_TOPPED = 3;
    private static final long ALL_USER_ID = -1L;

    @Resource
    private AnnouncementMapper announcementMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建公告
     * 前端请求只能创建全体用户的公告，不能创建系统通知
     */
    public void createAnnouncement(CreateAnnouncementRequest request) {
        // 前端创建的公告必须是全体用户的公告
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .publishedAt(getPublishedAt(request.getStatus(), request.getPublishedAt()))
                .status(request.getStatus())
                .signatory(request.getSignatory())
                .sticky(false)
                .targetUid(ALL_USER_ID)
                .build();
        announcementMapper.insert(announcement);
    }

    /**
     * 创建系统通知（供RPC接口使用）
     */
    public void sendSystemNotification(String title, String content, Long targetUserId) {
        Announcement notification = Announcement.builder()
                .title(title)
                .content(content)
                .type(AnnouncementTypeEnum.SYSTEMATIC)
                .publishedAt(LocalDateTime.now())
                .status(AnnouncementStatusEnum.PUBLISHED)
                .signatory("系统管理员")
                .sticky(false)
                .targetUid(targetUserId)
                .build();
        announcementMapper.insert(notification);
    }

    /**
     * 编辑公告
     */
    public void editAnnouncement(EditAnnouncementRequest request) {
        Announcement announcement = announcementMapper.selectById(request.getId());
        if (announcement == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (announcement.getTargetUid() != ALL_USER_ID) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setSignatory(request.getSignatory());

        if (announcement.getPublishedAt() == null || announcement.getPublishedAt().isAfter(LocalDateTime.now())) {
            announcement.setStatus(request.getStatus());
            announcement.setPublishedAt(getPublishedAt(request.getStatus(), request.getPublishedAt()));
            announcement.setType(request.getType());
        } else {
            // 如果尝试修改状态、发布时间、类型等关键字段，则报错（兜底用的）
            if ((request.getPublishedAt() != null && !request.getPublishedAt().equals(announcement.getPublishedAt()))
                    || (request.getStatus() != null && !request.getStatus().equals(announcement.getStatus()))
                    || (request.getType() != null && !request.getType().equals(announcement.getType()))) {
                throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
            }
        }

        announcementMapper.updateById(announcement);
    }

    /**
     * 删除公告
     */
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        announcementMapper.deleteById(id);
    }

    /**
     * 设置公告置顶
     */
    public void stickyAnnouncement(Long id, Boolean isSticky) {
        long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                .ne(Announcement::getId, id)
                .eq(Announcement::getSticky, true));
        if (count >= DEFAULT_TOPPED && Boolean.TRUE.equals(isSticky)) {
            throw new ApiException(ExceptionEnum.ANNOUNCEMENT_STICKY_LIMIT_REACHED);
        }
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        if (announcement.getTargetUid() != ALL_USER_ID) {
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
        }
        if (announcement.getPublishedAt() == null || announcement.getPublishedAt().isAfter(LocalDateTime.now())) {
            throw new ApiException(ExceptionEnum.ANNOUNCEMENT_NOT_PUBLISHED);
        }
        announcement.setSticky(isSticky);
        announcementMapper.updateById(announcement);
    }

    /**
     * 检查用户是否有权限修改该公告
     */
    public boolean hasPermission(Long announcementId, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        if (user.getRole() == UserTypeEnum.SUPER_ADMIN) {
            return true;
        }
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        return user.getId().equals(announcement.getCreateUid());
    }

    /**
     * 获取置顶公告（不足三个则用最新补充）
     * 只显示全体用户的公告，不包含系统通知
     */
    public List<Announcement> getTopAnnouncements() {
        return announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                .ne(Announcement::getStatus, AnnouncementStatusEnum.DRAFT)
                .le(Announcement::getPublishedAt, LocalDateTime.now())
                .eq(Announcement::getTargetUid, ALL_USER_ID)
                .orderByDesc(Announcement::getSticky)
                .orderByDesc(Announcement::getUpdatedAt)
                .last("LIMIT 6"));
    }

    /**
     * 校验并获取合法的公告发布时间
     */
    private LocalDateTime getPublishedAt(AnnouncementStatusEnum status, LocalDateTime publishedTime) {
        LocalDateTime publishedAt = null;
        if (status == AnnouncementStatusEnum.PUBLISHED) {
            publishedAt = LocalDateTime.now();
        } else if (status == AnnouncementStatusEnum.SCHEDULED) {
            if (publishedTime == null || publishedTime.isBefore(LocalDateTime.now())) {
                throw new ApiException(ExceptionEnum.INVALID_PARAMETER);
            }
            publishedAt = publishedTime;
        }
        return publishedAt;
    }

    public GetAdminAnnouncementDetailResponse getAdminAnnouncementDetail(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        long currentUid = StpUtil.getLoginIdAsLong();
        if (announcement == null) {
            throw new ApiException(ExceptionEnum.RESOURCE_NOT_FOUND);
        }
        return GetAdminAnnouncementDetailResponse.builder()
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .type(announcement.getType())
                .status(getTrueStatus(announcement.getPublishedAt(), announcement.getStatus()))
                .signatory(announcement.getSignatory())
                .updatedAt(announcement.getUpdatedAt())
                .sticky(announcement.getSticky())
                .publishedAt(announcement.getPublishedAt())
                .publisher(getPublisher(announcement.getCreateUid()))
                .editable(hasPermission(id, currentUid))
                .build();
    }

    /**
     * 用户公告列表
     */
    public BaseListResponse<GetAnnouncementListElement> userListAnnouncements(Integer page, Integer pageSize, AnnouncementTypeEnum type) {
        long currentUserId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(currentUserId);
        user.setLastAnnouncementReadAt(LocalDateTime.now());
        userMapper.updateById(user);

        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<Announcement>()
                .ne(Announcement::getStatus, AnnouncementStatusEnum.DRAFT)
                .le(Announcement::getPublishedAt, LocalDateTime.now());
        if (type != null) {
            // 指定类型查询
            if (type == AnnouncementTypeEnum.SYSTEMATIC) {
                queryWrapper.eq(Announcement::getType, AnnouncementTypeEnum.SYSTEMATIC)
                        .and(wrapper -> wrapper.eq(Announcement::getTargetUid, ALL_USER_ID)
                                .or().eq(Announcement::getTargetUid, currentUserId));
            } else {
                queryWrapper.eq(Announcement::getType, type)
                        .eq(Announcement::getTargetUid, ALL_USER_ID);
            }
        } else {
            queryWrapper.and(wrapper -> wrapper.eq(Announcement::getTargetUid, ALL_USER_ID)
                    .or().eq(Announcement::getTargetUid, currentUserId));
        }

        queryWrapper.orderByDesc(Announcement::getSticky)
                .orderByDesc(Announcement::getPublishedAt);
        IPage<Announcement> pageResult = new Page<>(page, pageSize);
        announcementMapper.selectPage(pageResult, queryWrapper);

        List<GetAnnouncementListElement> list = pageResult.getRecords().stream()
                .map(announcement -> GetAnnouncementListElement.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .content(announcement.getContent())
                        .type(announcement.getType())
                        .signatory(announcement.getSignatory())
                        .publishedAt(announcement.getPublishedAt())
                        .sticky(announcement.getSticky())
                        .build())
                .toList();

        return BaseListResponse.<GetAnnouncementListElement>builder()
                .list(list)
                .page(page)
                .pageSize(pageSize)
                .total(pageResult.getTotal())
                .build();
    }

    /**
     * 获取发布者昵称
     */
    private String getPublisher(Long createUid) {
        User user = userMapper.selectById(createUid);
        return user == null ? "" : user.getNickname();
    }

    /**
     * 管理员公告列表及查询功能（不管对私的通知）
     */
    public BaseListResponse<GetAdminAnnouncementListElement> adminQueryAnnouncements(Integer page, Integer pageSize, AnnouncementTypeEnum type, AnnouncementStatusEnum status, String order, String keyword) {
        long currentUid = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null, Announcement::getType, type)
                .eq(Announcement::getTargetUid, ALL_USER_ID)
                .orderByDesc(Announcement::getSticky);
        boolean isAsc = "asc".equals(order);
        if (status == AnnouncementStatusEnum.DRAFT) {
            queryWrapper.eq(Announcement::getStatus, AnnouncementStatusEnum.DRAFT);
            queryWrapper.orderBy(true, isAsc, Announcement::getUpdatedAt);
        }
        if (status == AnnouncementStatusEnum.PUBLISHED) {
            queryWrapper.ne(Announcement::getStatus, AnnouncementStatusEnum.DRAFT)
                    .le(Announcement::getPublishedAt, LocalDateTime.now());
            queryWrapper.orderBy(true, isAsc, Announcement::getPublishedAt);
        }
        if (status == AnnouncementStatusEnum.SCHEDULED) {
            queryWrapper.ne(Announcement::getStatus, AnnouncementStatusEnum.DRAFT)
                    .gt(Announcement::getPublishedAt, LocalDateTime.now());
            queryWrapper.orderBy(true, isAsc, Announcement::getPublishedAt);
        }
        if (status == null) {
            queryWrapper.orderBy(true, isAsc, Announcement::getUpdatedAt);
        }
        queryWrapper.like(StringUtils.isNotBlank(keyword), Announcement::getTitle, keyword);

        IPage<Announcement> pageResult = new Page<>(page, pageSize);
        announcementMapper.selectPage(pageResult, queryWrapper);
        List<GetAdminAnnouncementListElement> list = pageResult.getRecords().stream()
                .map(announcement -> GetAdminAnnouncementListElement.builder()
                        .id(announcement.getId())
                        .title(announcement.getTitle())
                        .type(announcement.getType())
                        .status(getTrueStatus(announcement.getPublishedAt(), announcement.getStatus()))
                        .publishedAt(announcement.getPublishedAt())
                        .updatedAt(announcement.getUpdatedAt())
                        .sticky(announcement.getSticky())
                        .editable(hasPermission(announcement.getId(), currentUid))
                        .build()
                ).toList();
        return BaseListResponse.<GetAdminAnnouncementListElement>builder()
                .list(list)
                .page(page)
                .pageSize(pageSize)
                .total(pageResult.getTotal())
                .build();
    }

    /**
     * 获取正确的公告状态
     */
    private AnnouncementStatusEnum getTrueStatus(LocalDateTime publishedAt, AnnouncementStatusEnum status) {
        if (publishedAt == null) {
            return AnnouncementStatusEnum.DRAFT;
        }
        return LocalDateTime.now().isBefore(publishedAt) ? status : AnnouncementStatusEnum.PUBLISHED;
    }
}

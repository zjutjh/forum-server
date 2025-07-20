package org.jh.forum.server.dubbo;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.service.AnnouncementService;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.manager.AnnouncementManager;
import org.jh.forum.server.manager.UserManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 公告服务实现类
 *
 * @author SituChengxiang (SK)
 */
@Slf4j
@Service
@DubboService
public class AnnouncementServiceImpl implements AnnouncementService {

    /**
     * 分页参数常量定义
     */
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final int MAX_CONTENT_LENGTH = 500;
    private static final int MAX_TITLE_LENGTH = 50;
    private static final int MIN_CONTENT_LENGTH = 2;
    private static final String NO_CONDITION = "all";

    @Resource
    private AnnouncementManager announcementManager;
    @Resource
    private UserManager userManager;

    /**
     * 创建公告
     */
    @Override
    public AnnouncementOperationResponse createAnnouncement(CreateAnnouncementRequest request) {
        try {

            if (!StpUtil.hasRole("super_admin") && !StpUtil.hasRole("admin")) {
                log.warn("用户无权限创建公告, 用户ID: {}", StpUtil.getLoginIdAsLong());
                throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }

            // 标题和内容基本校验
            validateTitleAndContent(request.getTitle(), request.getContent());

            // 校验公告类型
            validateAnnouncementType(request.getType());

            // 校验定时发布和状态逻辑
            validateScheduledAndStatus(request.getScheduledAt(), request.getStatus());
            LocalDateTime publishedAt = null;
            if (request.getStatus().equals(AnnouncementStatusEnum.SCHEDULED.getCode())) {
                // 预发布的，先设置发布时间为预发布
                publishedAt = request.getScheduledAt();
            } else if (request.getStatus().equals(AnnouncementStatusEnum.PUBLISHED.getCode())) {
                // 直接发布的，选择当前时间作为发布时间
                publishedAt = LocalDateTime.now();
            }

            // 如果设置为置顶, 检查置顶公告数量限制 (最多3个)
            if (Boolean.TRUE.equals(request.getSticky()) && !announcementManager.canStickyAnnouncement()) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            Announcement saved = announcementManager.createAnnouncement(request, publishedAt);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnouncementId(saved.getId());
            return response;
        } catch (IllegalArgumentException e) {
            // 包装参数错误
            log.warn("创建公告参数校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            // Manager的异常
            log.warn("创建公告异常:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // 别的异常
            log.error("创建公告未知异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 编辑公告
     */
    @Override
    public AnnouncementOperationResponse editAnnouncement(EditAnnouncementRequest request) {
        // 从SA-Token获取当前用户信息
        Long currentUid = StpUtil.getLoginIdAsLong();
        boolean isSuperAdmin = StpUtil.hasRole("super_admin");

        log.info("Service-编辑公告, ID:{}, 标题:{}", request.getId(), request.getTitle());
        try {

            if (!announcementManager.isExist(request.getId())) {
                throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
            }


            if (!checkPermission(request.getId(), currentUid, isSuperAdmin)) {
                log.warn("用户无权限更新公告, 公告ID: {}, 用户ID: {}", request.getId(), currentUid);
                throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }

            // 校验标题和内容
            validateTitleAndContent(request.getTitle(), request.getContent());

            // 校验公告类型
            validateAnnouncementType(request.getType());

            // 校验定时发布和状态逻辑
            validateScheduledAndStatus(request.getScheduledAt(), request.getStatus());
            LocalDateTime publishedAt = null;
            if (request.getScheduledAt() != null
                    && request.getStatus().equals(AnnouncementStatusEnum.SCHEDULED.getCode())) {
                // 预发布的，先设置发布时间为预发布
                publishedAt = request.getScheduledAt();
            } else if (request.getStatus().equals(AnnouncementStatusEnum.PUBLISHED.getCode())) {
                publishedAt = LocalDateTime.now();
            }

            // 是否存在
            if (!announcementManager.isExist(request.getId())) {
                throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
            }

            Announcement originAnnouncement = announcementManager.getAnnouncementEntityById(request.getId());

            // 如果设置为置顶, 检查置顶公告数量限制 (最多3个)
            if (request.getSticky() != null && request.getSticky()
                    && !announcementManager.canStickyAnnouncement(request.getId())) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            // 内联权限状态检验
            if (originAnnouncement.getStatus() == AnnouncementStatusEnum.PUBLISHED) {
                // 如果当前公告为已发布状态, 则不允许编辑定时发布和状态
                if (request.getScheduledAt() != null || null != originAnnouncement.getScheduledAt()
                        || !request.getStatus().equals(AnnouncementStatusEnum.PUBLISHED.getCode())) {
                    throw new IllegalArgumentException("已发布的公告不允许编辑定时发布和状态");
                }
                // 执行基础字段更新 (只能编辑标题、内容、类型、属性、置顶)
                return announcementManager.editBasicFields(request.getId(), request);
            } else {
                // 草稿和待发布状态的公告可以编辑所有字段
                return announcementManager.editAllFields(request.getId(), request, publishedAt);
            }

        } catch (IllegalArgumentException e) {
            log.warn("编辑公告参数校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.warn("编辑公告异常:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("编辑公告异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 置顶/取消置顶公告
     */
    @Override
    public AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean sticky) {
        // 从SA-Token获取当前用户信息
        Long currentUid = StpUtil.getLoginIdAsLong();
        boolean isSuperAdmin = StpUtil.hasRole("super_admin");

        try {
            log.info("置顶/取消置顶公告, ID:{}, 置顶状态:{}", id, sticky);

            // 校验sticky参数 (防御性编程)
            if (sticky == null) {
                throw new IllegalArgumentException("置顶状态不能为空, 必须为true或false");
            }

            if (!checkPermission(id, currentUid, isSuperAdmin)) {
                log.warn("用户无权限修改公告置顶状态, 公告ID: {}, 用户ID: {}", id, currentUid);
                throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }

            // 校验ID并检查公告是否存在且未被删除
            if (!announcementManager.isExist(id)) {
                throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
            }

            // 如果置顶, 检查置顶公告数量限制 (最多3个)
            if (sticky && !announcementManager.canStickyAnnouncement(id)) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            return announcementManager.stickyAnnouncement(id, sticky);

        } catch (IllegalArgumentException e) {
            log.warn("修改置顶状态参数校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.warn("manager-置顶/取消置顶失败:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("置顶/取消公告异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 删除公告
     */
    @Override
    public AnnouncementOperationResponse deleteAnnouncement(Long id) {
        // 从SA-Token获取当前用户信息
        Long currentUid = StpUtil.getLoginIdAsLong();
        boolean isSuperAdmin = StpUtil.hasRole("super_admin");

        try {

            if (!checkPermission(id, currentUid, isSuperAdmin)) {
                log.warn("用户无权限删除公告, 公告ID: {}, 用户ID: {}", id, currentUid);
                throw new ApiException(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }

            log.info("删除公告, ID:{}", id);
            if (!announcementManager.isExist(id)) {
                throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
            }
            return announcementManager.deleteAnnouncement(id);
        } catch (IllegalArgumentException e) {
            log.warn("删除公告参数校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.warn("删除公告失败:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除公告异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 根据ID查询公告详情 (管理员)
     */
    @Override
    public AnnouncementDetailResponse getAnnouncementById(Long id) {
        try {
            log.info("查询公告详情, ID:{}", id);

            if (!announcementManager.isExist(id)) {
                throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
            }

            AnnouncementDetailResponse response = new AnnouncementDetailResponse();
            Announcement raw = announcementManager.getAnnouncementEntityById(id);
            response.setId(raw.getId());
            response.setTitle(raw.getTitle());
            response.setContent(raw.getContent());
            response.setType(raw.getType());
            response.setStatus(raw.getStatus());
            response.setCreator(getUsernameById(raw.getCreateUid()));
            response.setUpdater(getUsernameById(raw.getUpdateUid()));

            // 使用实际的时间数据和时间数据
            response.setCreatedAt(raw.getCreatedAt());
            response.setUpdatedAt(raw.getUpdatedAt());
            response.setScheduledAt(raw.getScheduledAt());
            response.setPublishedAt(raw.getPublishedAt());
            response.setAttribute(raw.getAttribute());
            response.setSticky(raw.getSticky());

            return response;
        } catch (IllegalArgumentException e) {
            log.warn("admin查询公告详情校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.warn("查询公告详情异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("查询公告详情未知异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 根据ID查询公告详情 (用户版本)
     */
    @Override
    public AnnouncementTinyDetailsResponse getAnnouncementTinyDetailsById(Long id) {
        try {
            log.debug("查询公告详情 (用户版) , ID:{}", id);

            if (!announcementManager.isExist(id)) {
                throw new ApiException(ExceptionEnum.NOT_FOUND_ERROR);
            }

            AnnouncementTinyDetailsResponse response = new AnnouncementTinyDetailsResponse();
            Announcement raw = announcementManager.getAnnouncementEntityById(id);

            // 检查公告状态
            // 被删除或者草稿公告 一定不可见
            if (raw.getDeleted() || raw.getStatus().equals(AnnouncementStatusEnum.DRAFT)) {
                throw new IllegalArgumentException("公告状态异常");
            } else if (raw.getStatus().equals(AnnouncementStatusEnum.SCHEDULED)
                    && (raw.getScheduledAt() == null || raw.getScheduledAt().isAfter(LocalDateTime.now()))) {
                // 状态为待发布且预定发布时间在当前时间之后的（一定是未发布的）不可见
                throw new IllegalArgumentException("公告状态异常");
            }

            response.setId(id);
            response.setTitle(raw.getTitle());
            response.setContent(raw.getContent());
            response.setType(raw.getType());
            response.setSticky(raw.getSticky());

            // 填充用户信息
            response.setCreator(getUsernameById(raw.getCreateUid()));
            response.setUpdater(getUsernameById(raw.getUpdateUid()));

            // 格式化时间
            response.setUpdatedAt(raw.getUpdatedAt());
            response.setPublishedAt(raw.getPublishedAt());

            return response;
        } catch (IllegalArgumentException e) {
            log.warn("user查询公告详情校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.error("查询公告详情数据库操作异常", e);
            throw e;
        } catch (Exception e) {
            log.error("查询公告详情未知异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 查询公告列表(用户版本)
     */
    @Override
    public BaseListResponse<ListAnnouncementTinyItemResponse> userListAnnouncements(
            UserQueryAnnouncementRequest request) {
        try {
            log.debug("用户查询公告列表, 页码:{}, 大小:{}, 类型: {}", request.getPage(), request.getPageSize(), request.getType());

            // page：不传或 <1 时都设为 1
            request.setPage(
                    request.getPage() == null || request.getPage() < 1
                            ? MIN_PAGE_SIZE
                            : request.getPage());

            // pageSize：不传或超出范围时都设为 DEFAULT_PAGE_SIZE
            request.setPageSize(
                    request.getPageSize() == null
                            || request.getPageSize() < MIN_PAGE_SIZE
                            || request.getPageSize() > MAX_PAGE_SIZE
                            ? DEFAULT_PAGE_SIZE
                            : request.getPageSize());

            // 处理类型转换
            if (request.getType() != null) {
                switch (request.getType()) {
                    case "systematic":
                        request.setType(AnnouncementTypeEnum.SYSTEMATIC.getCode());
                        break;
                    case "scholastic":
                        request.setType(AnnouncementTypeEnum.SCHOLASTIC.getCode());
                        break;
                    default:
                        request.setType("all");
                        break;
                }
            } else {
                request.setType("all");
            }

            // 调用Manager层获取分页数据
            IPage<Announcement> pageResult = announcementManager
                    .findUserAnnouncementsWithPaging(request);

            // 【批量优化】1. 收集所有需要的用户ID
            Set<Long> userIds = pageResult.getRecords().stream()
                    .flatMap(announcement -> Stream.of(
                            announcement.getCreateUid(),
                            announcement.getUpdateUid()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 【批量优化】2. 批量获取用户昵称
            Map<Long, String> nicknameMap = getUsernamesByIds(userIds);

            // 【批量优化】3. 转换为响应对象
            List<ListAnnouncementTinyItemResponse> itemList = pageResult.getRecords().stream()
                    .map(announcement -> convertToUserAnnouncementItem(announcement, nicknameMap))
                    .toList();

            // 构建分页响应
            BaseListResponse<ListAnnouncementTinyItemResponse> response = BaseListResponse
                    .<ListAnnouncementTinyItemResponse>builder()
                    .total(pageResult.getTotal())
                    .page(pageResult.getCurrent() <= Integer.MAX_VALUE ? (int) pageResult.getCurrent() : 1)
                    .pageSize(pageResult.getSize() <= Integer.MAX_VALUE ? (int) pageResult.getSize() : 10)
                    .list(itemList)
                    .build();

            log.debug("查询公告列表成功, 总数: {}, 当前页数据: {}", pageResult.getTotal(), itemList.size());
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("查询公告列表校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.warn("sys-查询公告列表异常:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("查询公告列表未知异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 将 Announcement 实体转换为用户版 ListAnnouncementTinyItemResponse
     */
    private ListAnnouncementTinyItemResponse convertToUserAnnouncementItem(
            Announcement announcement, Map<Long, String> nicknameMap) {

        try {
            ListAnnouncementTinyItemResponse item = new ListAnnouncementTinyItemResponse();
            item.setId(announcement.getId());
            item.setTitle(announcement.getTitle());
            item.setType(announcement.getType());
            item.setSticky(announcement.getSticky());

            // 【批量优化】设置用户名
            item.setCreator(nicknameMap.get(announcement.getCreateUid()));
            item.setUpdater(nicknameMap.get(announcement.getUpdateUid()));

            // 格式化时间 - 用户版本相对简化
            item.setUpdatedAt(announcement.getUpdatedAt());
            item.setPublishedAt(announcement.getPublishedAt());

            return item;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户版实体到列表失败, 异常: {}", e.getMessage(), e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 管理员查询公告列表
     */
    @Override
    public BaseListResponse<ListAnnouncementItemResponse> adminQueryAnnouncements(
            AdminQueryAnnouncementRequest request) {
        try {
            log.debug("管理员查询公告列表, 页码:{}, 状态:{},类型:{}, 排序方向:{}",
                    request.getPage(), request.getStatus(), request.getType(), request.orderType());

            // page：不传或 <1 时都设为 1
            request.setPage(
                    request.getPage() == null || request.getPage() < 1
                            ? MIN_PAGE_SIZE
                            : request.getPage());

            // pageSize：不传或超出范围时都设为 DEFAULT_PAGE_SIZE
            request.setPageSize(
                    request.getPageSize() == null
                            || request.getPageSize() < MIN_PAGE_SIZE
                            || request.getPageSize() > MAX_PAGE_SIZE
                            ? DEFAULT_PAGE_SIZE
                            : request.getPageSize());

            if (request.getStatus() != null && !NO_CONDITION.equals(request.getStatus())
                    && !request.getStatus().equals(AnnouncementStatusEnum.DRAFT.getCode())
                    && !request.getStatus().equals(AnnouncementStatusEnum.PUBLISHED.getCode())
                    && !request.getStatus().equals(AnnouncementStatusEnum.SCHEDULED.getCode())) {
                throw new IllegalArgumentException("状态值条件无效, 必须为draft, published, scheduled 或 all(null)");
            }
            if (request.orderType() < 0 || request.orderType() > 1) {
                throw new IllegalArgumentException("排序方向必须为0 (升序) 或1 (降序) ");
            }

            // 处理状态转换
            if (request.getStatus() != null) {
                switch (request.getStatus()) {
                    case "draft":
                        request.setStatus(AnnouncementStatusEnum.DRAFT.getCode());
                        break;
                    case "published":
                        request.setStatus(AnnouncementStatusEnum.PUBLISHED.getCode());
                        break;
                    case "scheduled":
                        request.setStatus(AnnouncementStatusEnum.SCHEDULED.getCode());
                        break;
                    default:
                        request.setStatus("all");
                        break;
                }
            } else {
                request.setStatus("all");
            }
            // 处理类型转换
            if (request.getType() != null) {
                switch (request.getType()) {
                    case "systematic":
                        request.setType(AnnouncementTypeEnum.SYSTEMATIC.getCode());
                        break;
                    case "scholastic":
                        request.setType(AnnouncementTypeEnum.SCHOLASTIC.getCode());
                        break;
                    default:
                        request.setType("all");
                        break;
                }
            } else {
                request.setType("all");
            }

            // 调用Manager层获取分页数据
            IPage<Announcement> pageResult = announcementManager
                    .findAdminAnnouncementsWithPaging(request);

            // 【批量优化】1. 收集所有需要的用户ID
            Set<Long> userIds = pageResult.getRecords().stream()
                    .flatMap(announcement -> Stream.of(
                            announcement.getCreateUid(),
                            announcement.getUpdateUid()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 【批量优化】2. 批量获取用户昵称
            Map<Long, String> nicknameMap = getUsernamesByIds(userIds);

            // 【批量优化】3. 转换为响应对象
            List<ListAnnouncementItemResponse> itemList = pageResult.getRecords().stream()
                    .map(announcement -> convertToAdminAnnouncementItem(announcement, nicknameMap))
                    .toList();

            // 构建分页响应
            BaseListResponse<ListAnnouncementItemResponse> response = BaseListResponse
                    .<ListAnnouncementItemResponse>builder()
                    .total(pageResult.getTotal())
                    .page(pageResult.getCurrent() <= Integer.MAX_VALUE ? (int) pageResult.getCurrent() : 1)
                    .pageSize(pageResult.getSize() <= Integer.MAX_VALUE ? (int) pageResult.getSize() : 10)
                    .list(itemList)
                    .build();

            log.debug("管理员查询公告列表成功, 总数: {}, 当前页数据: {}", pageResult.getTotal(), itemList.size());
            return response;

        } catch (IllegalArgumentException e) {
            log.warn("管理员查询公告列表校验失败: {}", e.getMessage());
            throw new ApiException(ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ApiException e) {
            log.warn("查询公告列表异常:{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("查询公告列表未知异常", e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 将 Announcement 实体转换为管理员版 ListAnnouncementItemResponse
     */
    private ListAnnouncementItemResponse convertToAdminAnnouncementItem(
            Announcement announcement, Map<Long, String> nicknameMap) {

        try {
            ListAnnouncementItemResponse item = new ListAnnouncementItemResponse();
            item.setId(announcement.getId());
            item.setTitle(announcement.getTitle());
            item.setType(announcement.getType());
            item.setStatus(announcement.getStatus());
            item.setSticky(announcement.getSticky());

            // 【批量优化】设置用户名
            item.setCreator(nicknameMap.get(announcement.getCreateUid()));
            item.setUpdater(nicknameMap.get(announcement.getUpdateUid()));

            // 格式化时间 - 管理员版包含完整时间信息
            item.setCreatedAt(announcement.getCreatedAt());
            item.setUpdatedAt(announcement.getUpdatedAt());
            item.setScheduledAt(announcement.getScheduledAt());
            item.setPublishedAt(announcement.getPublishedAt());

            return item;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员版实体到列表失败, 异常: {}", e.getMessage(), e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 获取置顶的公告列表（凑满3个）
     */
    private ListAnnouncementMinorResponse.AnnouncementMinorResponse toItemDto(Announcement announcement) {
        return new ListAnnouncementMinorResponse.AnnouncementMinorResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getSticky());
    }

    @Override
    public ListAnnouncementMinorResponse getTopAnnouncements() {
        try {
            List<Announcement> stickies = announcementManager.getStickyAnnouncements();
            List<Announcement> recents = announcementManager.getRecentAnnouncements();

            // Stream 流中的类型是嵌套的 record 类型
            List<ListAnnouncementMinorResponse.AnnouncementMinorResponse> topAnnouncements = Stream
                    .concat(stickies.stream(), recents.stream())
                    .distinct()
                    .limit(3)
                    .map(this::toItemDto)
                    .collect(Collectors.toList());

            // 使用处理过的列表创建并返回最终响应对象
            return new ListAnnouncementMinorResponse(topAnnouncements);

        } catch (Exception e) {
            log.error("获取首页公告失败, 异常: {}", e.getMessage(), e);
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }
    }

    /**
     * 辅助方法-编辑/删除权限校验
     */
    public boolean checkPermission(Long announcementId, Long userId, boolean isSuperAdmin) {
        if (!isSuperAdmin) {
            try {
                // 调用Manager层方法检查是否为创建者
                return announcementManager.isAnnouncementCreator(announcementId, userId);
            } catch (Exception e) {
                log.error("检查公告创建者异常", e);
                throw new ApiException(ExceptionEnum.SERVER_ERROR);
            }
        } else {
            return true;
        }
    }

    /**
     * 校验标题和内容长度 (Service防御)
     */
    private void validateTitleAndContent(String title, String content) {
        // 校验标题长度 (2-50个字符)
        String trimmedTitle = title != null ? title.trim() : null;
        if (trimmedTitle == null || trimmedTitle.length() < MIN_CONTENT_LENGTH
                || trimmedTitle.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("公告标题长度必须在2-50字符之间");
        }

        // 校验内容长度 (2-500个字符)
        String trimmedContent = content != null ? content.trim() : null;
        if (trimmedContent == null || trimmedContent.length() < MIN_CONTENT_LENGTH
                || trimmedContent.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("公告内容长度必须在2-500字符之间");
        }
    }

    /**
     * 校验公告类型 (Service防御)
     */
    private void validateAnnouncementType(String type) {
        if (!type.equals(AnnouncementTypeEnum.SCHOLASTIC.getCode())
                && !type.equalsIgnoreCase(AnnouncementTypeEnum.SYSTEMATIC.getCode())) {
            throw new IllegalArgumentException("公告类型无效, 仅支持系统公告(systematic)和学校公告(scholastic)");
        }
    }

    /**
     * 校验创建时的定时发布和状态逻辑
     */
    private void validateScheduledAndStatus(LocalDateTime scheduledAt, String status) {
        if (scheduledAt != null) {
            // 获取当前时区 (UTC+8) 时间
            LocalDateTime now = LocalDateTime.now();
            // 设置最小允许时间 (当前时间+30秒)
            LocalDateTime minAllowedTime = now.plusSeconds(30);

            // 比较带时区的时间
            if (scheduledAt.isBefore(minAllowedTime)) {
                throw new IllegalArgumentException("定时发布时间必须至少在当前时间30秒之后");
            }

            // scheduled_at非空时, status只能为2
            if (status == null || !status.equals(AnnouncementStatusEnum.SCHEDULED.getCode())) {
                throw new IllegalArgumentException("已设置定时发布, 状态已锁定");
            }
        } else {
            // scheduled_at为空时, status可以为0或1
            if (status != null && !status.equals(AnnouncementStatusEnum.DRAFT.getCode())
                    && !status.equals(AnnouncementStatusEnum.PUBLISHED.getCode())) {
                throw new IllegalArgumentException("未设置定时发布时, 状态只能为草稿或已发布");
            }
        }
    }

    /**
     * 辅助方法-根据id批量获取用户昵称（使用缓存）
     */
    private Map<Long, String> getUsernamesByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return userManager.getUsernamesByIds(userIds);
    }

    /**
     * 获取单个用户名（使用缓存）
     */
    private String getUsernameById(Long userId) {
        if (userId == null) {
            return "";
        }
        var userInfo = userManager.getUserInfo(userId);
        return userInfo != null && userInfo.getNickname() != null ? userInfo.getNickname() : "";
    }

}
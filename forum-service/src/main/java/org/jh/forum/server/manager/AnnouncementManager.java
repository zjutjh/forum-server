package org.jh.forum.server.manager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.server.mapper.AnnouncementMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告业务管理层
 * 负责处理公告相关的业务逻辑
 * 
 * @author SituChengxiang
 */
@Slf4j
@Component
public class AnnouncementManager {
    @Resource
    private AnnouncementMapper announcementMapper;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        log.info("公告管理器时区已设置为 Asia/Shanghai (UTC+8)");
    }

    /**
     * 格式化为ISO-8601字符串（用于响应）
     * 注意：数据库存储的是 UTC+8 本地时间，直接格式化输出
     */
    public String formatToIso8601(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        // 直接格式化，不做时区转换，因为数据库存储的就是 UTC+8 时间
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    /**
     * 创建公告 - 原子数据库操作
     */
    @Transactional
    public Announcement createAnnouncement(Announcement entity) {
        log.info("Manager层执行数据库插入操作，公告标题：{}", entity.getTitle());
        int result = announcementMapper.insert(entity);
        if (result > 0) {
            log.info("数据库插入成功，announcement_id: {}", entity.getId());
            return entity;
        } else {
            throw new RuntimeException("数据库插入失败");
        }
    }

    /**
     * 编辑公告 - 原子数据库操作
     */
    public AnnouncementOperationResponse editAnnouncement(Long id, EditAnnouncementRequest request) {
        log.info("Manager层执行数据库更新操作，ID：{}，标题：{}", id, request.getTitle());

        // 构建要更新的实体对象
        Announcement updateEntity = Announcement.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .scheduledAt(request.getScheduledAt())
                .status(request.getStatus())
                .attribute(convertAttributeToString(request.getAttribute()))
                .build();

        // 使用MyBatis-Plus的updateById方法，会自动触发AutoFillHandler
        int result = announcementMapper.updateById(updateEntity);

        if (result <= 0) {
            throw new RuntimeException("数据库更新失败，数据库或公告状态异常");
        }

        log.info("数据库更新成功，ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 编辑基础字段 - 原子数据库操作（已发布公告只能编辑这些字段）
     * 允许编辑：title, content, type, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Transactional
    public AnnouncementOperationResponse editBasicFields(Long id, EditAnnouncementRequest request) {
        log.info("Manager层执行基础字段更新操作，ID：{}，标题：{}", id, request.getTitle());

        // 使用 MyBatis-Plus 的 updateById 方法，会自动触发 AutoFillHandler
        Announcement updateEntity = Announcement.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .attribute(convertAttributeToString(request.getAttribute()))
                .sticky(request.getSticky())
                .build();

        int result = announcementMapper.updateById(updateEntity);

        if (result <= 0) {
            throw new RuntimeException("数据库更新失败，数据库或公告状态异常");
        }

        log.info("基础字段更新成功，ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 编辑所有字段 - 原子数据库操作（草稿和待发布公告可以编辑所有字段）
     * 允许编辑：title, content, type, status, scheduled_at, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Transactional
    public AnnouncementOperationResponse editAllFields(Long id, EditAnnouncementRequest request) {
        log.info("Manager层执行所有字段更新操作，ID：{}，标题：{}", id, request.getTitle());

        // 使用 MyBatis-Plus 的 updateById 方法，会自动触发 AutoFillHandler
        Announcement updateEntity = Announcement.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .status(request.getStatus())
                .scheduledAt(request.getScheduledAt())
                .attribute(convertAttributeToString(request.getAttribute()))
                .sticky(request.getSticky())
                .build();

        int result = announcementMapper.updateById(updateEntity);

        if (result <= 0) {
            throw new RuntimeException("数据库更新失败，数据库或公告状态异常");
        }

        log.info("所有字段更新成功，ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 删除公告 - 原子数据库操作（软删除）
     */
    @Transactional
    public AnnouncementOperationResponse deleteAnnouncement(Long id) {
        log.info("Manager层执行数据库软删除操作，ID：{}", id);

        // 直接执行删除操作，移除重复的checkExist校验
        int result = announcementMapper.deleteById(id);

        if (result <= 0) {
            throw new RuntimeException("数据库删除失败，数据库或公告状态异常");
        }

        log.info("数据库软删除成功，ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 检查标题是否重复（创建时使用,已排除软删除公告）
     */
    public boolean checkTitleDuplicate(String title) {
        return announcementMapper.checkExistsByTitle(title);
    }

    /**
     * 检查标题是否重复（编辑时使用，排除当前公告ID，已排除软删除，后如无特殊情况不再注明）
     */
    public boolean checkTitleDuplicate(String title, Long excludeId) {
        return announcementMapper.checkExistsByTitleAndIdNot(title, excludeId);
    }

    /**
     * 将Object类型的attribute转换为String
     * 如果是null则返回null，如果是String则直接返回，否则转换为JSON字符串
     */
    private String convertAttributeToString(Object attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof String) {
            return (String) attribute;
        }
        // 对于其他类型，可以转换为JSON字符串
        // 这里先简单返回toString()，如果需要完整的JSON序列化可以使用Jackson
        return attribute.toString();
    }

    /**
     * 根据ID检查公告是否存在
     */
    public boolean checkExist(Long id) {
        // 保留实际数据访问逻辑
        return announcementMapper.checkExist(id);
    }

    /**
     * 根据ID获取公告实体（用于业务逻辑，返回实体对象）
     */
    public Announcement getAnnouncementEntityById(Long id) {
        log.info("Manager层查询公告实体，ID：{}", id);
        // 直接调用mapper查询，移除重复校验
        return announcementMapper.selectById(id);
    }

    /**
     * 根据ID查询公告
     */
    public AnnouncementDetailsResponse getAnnouncementById(Long id) {
        log.info("Manager层查询公告详情，ID：{}", id);

        // 查询公告实体
        Announcement raw = announcementMapper.findByID(id);

        if (raw == null) {
            throw new RuntimeException("公告不存在或已被删除");
        }

        AnnouncementDetailsResponse response = new AnnouncementDetailsResponse();
        response.setId(id);
        response.setTitle(raw.getTitle());
        response.setContent(raw.getContent());
        response.setType(raw.getType());
        response.setStatus(raw.getStatus());

        response.setCreator(getUsernameById(raw.getCreateUid()));
        response.setUpdator(getUsernameById(raw.getUpdateUid()));

        // 使用实际的时间数据和时间数据
        response.setCreatedAt(formatToIso8601(raw.getCreatedAt()));
        response.setUpdatedAt(formatToIso8601(raw.getUpdatedAt()));
        response.setScheduledAt(formatToIso8601(raw.getScheduledAt()));
        response.setAttribute(raw.getAttribute());
        response.setSticky(raw.getSticky());

        return response;
    }

    /**
     * 根据ID查询公告详情（用户版本）
     * 返回简化的公告信息，不包含管理员才需要的字段
     */
    public AnnouncementTinyDetailsResponse getAnnouncementTinyDetailsById(Long id) {
        log.info("Manager层查询公告详情（用户版），ID：{}", id);

        // 查询公告实体
        Announcement raw = announcementMapper.findByID(id);

        if (raw == null) {
            throw new RuntimeException("公告不存在或已被删除");
        }

        // 只返回用户需要的字段
        AnnouncementTinyDetailsResponse response = new AnnouncementTinyDetailsResponse();
        response.setId(id);
        response.setTitle(raw.getTitle());
        response.setContent(raw.getContent());
        response.setType(raw.getType());
        response.setSticky(raw.getSticky());

        // 填充用户信息
        response.setCreator(getUsernameById(raw.getCreateUid()));
        response.setUpdator(getUsernameById(raw.getUpdateUid()));

        // 格式化时间
        response.setCreatedAt(formatToIso8601(raw.getCreatedAt()));
        response.setUpdatedAt(formatToIso8601(raw.getUpdatedAt()));

        return response;
    }

    /**
     * TODO: 更好的对应
     * 辅助方法 暂时的id-username
     */

    public String getUsernameById(Long userId) {
        if (userId == null) {
            return "unknown";
        }

        // 临时映射表，便于测试
        Map<Long, String> tempMapping = Map.of(
                123L, "admin",
                -1L, "testuser",
                999L, "superadmin");

        return tempMapping.getOrDefault(userId, "user_" + userId);
    }

    /**
     * 批量获取用户名（性能优化，一次查询多个）
     */
    public Map<Long, String> getUsernamesByIds(Set<Long> userIds) {
        return userIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        this::getUsernameById));
    }

    /**
     * 分页查询公告列表
     */
    public ListAnnouncementResponse listAnnouncements(
            ListAnnouncementRequest request) {
        log.info(
                "Manager层查询公告列表，页码：{}，状态：{}，类型：{}",
                request.getPage(),
                request.getStatus(),
                request.getType());

        // 计算分页参数
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 8;
        int offset = (page - 1) * size;

        // 查询公告列表
        List<Announcement> announcements = announcementMapper.findAnnouncementsByPage(
                request.getStatus(),
                request.getType(),
                offset,
                size);

        // 查询总数
        Long totalCount = announcementMapper.countAnnouncements(
                request.getStatus(),
                request.getType());

        // 转换为响应对象
        List<ListAnnouncementResponse.AnnouncementItemResponse> itemList = announcements.stream()
                .map(this::convertToAnnouncementItem)
                .toList();
        ListAnnouncementResponse response = new ListAnnouncementResponse();
        response.setTotal(totalCount.intValue());
        response.setPage(page);
        response.setPageSize(size);
        response.setList(itemList);

        log.info("查询公告列表成功，总数: {}, 当前页数据: {}", totalCount, itemList.size());
        return response;
    }

    /**
     * 将 Announcement 实体转换为 AnnouncementItemResponse
     */
    private ListAnnouncementResponse.AnnouncementItemResponse convertToAnnouncementItem(Announcement announcement) {
        ListAnnouncementResponse.AnnouncementItemResponse item = new ListAnnouncementResponse.AnnouncementItemResponse();

        item.setId(announcement.getId());
        item.setTitle(announcement.getTitle());
        item.setType(announcement.getType());
        item.setStatus(announcement.getStatus());
        item.setSticky(announcement.getSticky());

        // 设置用户名
        item.setCreator(getUsernameById(announcement.getCreateUid()));
        item.setUpdator(getUsernameById(announcement.getUpdateUid()));

        // 格式化时间
        item.setCreatedAt(formatToIso8601(announcement.getCreatedAt()));
        item.setUpdatedAt(formatToIso8601(announcement.getUpdatedAt()));

        return item;
    }

    /**
     * 检查是否可以置顶公告（检查置顶数量限制）
     * 
     * @param excludeId 排除的公告ID（用于编辑时检查）
     * @return true表示可以置顶，false表示已达上限
     */
    public boolean canStickyAnnouncement(Long excludeId) {
        int count = announcementMapper.countStickyAnnouncementsExcludeId(excludeId);
        return count < 3; // 最多允许3个置顶公告
    }

    /**
     * 检查是否可以置顶公告（新增公告时使用）
     * 
     * @return true表示可以置顶，false表示已达上限，方法重载
     */
    public boolean canStickyAnnouncement() {
        int count = announcementMapper.countStickyAnnouncements();
        return count < 3; // 最多允许3个置顶公告
    }

    @Transactional
    public AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean isSticky) {
        log.info("Manager层置顶/取消置顶公告，ID：{}，置顶状态：{}", id, isSticky);

        // 构建要更新的实体对象（只更新sticky字段）
        Announcement updateEntity = Announcement.builder()
                .id(id)
                .sticky(isSticky)
                .build();

        // 使用MyBatis-Plus的updateById方法，会自动触发AutoFillHandler更新update_uid和updated_at
        int result = announcementMapper.updateById(updateEntity);

        if (result <= 0) {
            throw new RuntimeException("数据库更新失败，数据库或公告状态异常");
        }

        log.info("置顶状态更新成功，ID: {}, sticky: {}", id, isSticky);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 管理员查询公告列表
     * 支持三种状态筛选（草稿、已发布、待发布）+ 升序/降序排序 + 已删除数据查询
     */
    public ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request) {
        log.info("Manager层管理员查询公告列表，页码：{}，状态：{}，排序方向：{}，查询已删除：{}", 
                request.getPage(), request.getStatus(), request.orderType(), request.getDeleted());

        // 计算分页参数
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 8;
        int offset = (page - 1) * size;
        int orderType = request.orderType(); // 已有默认值处理
        boolean includeDeleted = request.getDeleted() != null ? request.getDeleted() : false;

        List<Announcement> announcements;
        Long totalCount;

        // 根据状态调用不同的查询方法
        switch (request.getStatus()) {
            case 0: // 草稿
                log.info("查询草稿公告列表，包含已删除：{}", includeDeleted);
                announcements = announcementMapper.findDraftAnnouncementsForAdmin(orderType, includeDeleted, offset, size);
                totalCount = announcementMapper.countAnnouncementsByStatusForAdmin(0, includeDeleted);
                break;
            case 1: // 已发布
                log.info("查询已发布公告列表，包含已删除：{}", includeDeleted);
                announcements = announcementMapper.findPublishedAnnouncementsForAdmin(orderType, includeDeleted, offset, size);
                totalCount = announcementMapper.countAnnouncementsByStatusForAdmin(1, includeDeleted);
                break;
            case 2: // 待发布
                log.info("查询待发布公告列表，包含已删除：{}", includeDeleted);
                announcements = announcementMapper.findScheduledAnnouncementsForAdmin(orderType, includeDeleted, offset, size);
                totalCount = announcementMapper.countAnnouncementsByStatusForAdmin(2, includeDeleted);
                break;
            default:
                log.warn("未知的公告状态：{}，默认查询已发布公告", request.getStatus());
                announcements = announcementMapper.findPublishedAnnouncementsForAdmin(orderType, includeDeleted, offset, size);
                totalCount = announcementMapper.countAnnouncementsByStatusForAdmin(1, includeDeleted);
                break;
        }

        // 转换为响应对象（管理员版本，包含完整字段）
        List<ListAnnouncementResponse.AnnouncementItemResponse> itemList = announcements.stream()
                .map(this::convertToAdminAnnouncementItem)
                .toList();

        // 构建响应
        ListAnnouncementResponse response = new ListAnnouncementResponse();
        response.setTotal(totalCount.intValue());
        response.setPage(page);
        response.setPageSize(size);
        response.setList(itemList);

        log.info("管理员查询公告列表完成，总数: {}, 当前页数据: {}", totalCount, itemList.size());
        return response;
    }

    /**
     * 统计指定状态的公告总数（管理员查询用）
     * @deprecated 使用 countAnnouncementsByStatusForAdmin 替代
     */
    @Deprecated
    private Long countAnnouncementsByStatus(Integer status) {
        return announcementMapper.countAnnouncements(status, null); // type为null表示不筛选类型
    }

    /**
     * 将 Announcement 实体转换为管理员版 AnnouncementItemResponse
     * 包含所有字段，包括 status, created_at, scheduled_at 等管理员需要的信息
     */
    private ListAnnouncementResponse.AnnouncementItemResponse convertToAdminAnnouncementItem(Announcement announcement) {
        ListAnnouncementResponse.AnnouncementItemResponse item = new ListAnnouncementResponse.AnnouncementItemResponse();

        item.setId(announcement.getId());
        item.setTitle(announcement.getTitle());
        item.setType(announcement.getType());
        item.setStatus(announcement.getStatus()); // 管理员版包含状态字段
        item.setSticky(announcement.getSticky());

        // 设置用户名
        item.setCreator(getUsernameById(announcement.getCreateUid()));
        item.setUpdator(getUsernameById(announcement.getUpdateUid()));

        // 格式化时间 - 管理员版包含完整时间信息
        item.setCreatedAt(formatToIso8601(announcement.getCreatedAt()));
        item.setUpdatedAt(formatToIso8601(announcement.getUpdatedAt()));
        
        // 预定发布时间 - 可能为null
        if (announcement.getScheduledAt() != null) {
            item.setScheduledAt(formatToIso8601(announcement.getScheduledAt()));
        }

        return item;
    }

    /**
     * 查询到期的待发布公告
     * 用于定时发布功能
     */
    public List<Announcement> findExpiredScheduledAnnouncements() {
        LocalDateTime currentTime = LocalDateTime.now();
        log.info("Manager层查询到期的待发布公告，当前时间：{}", formatToIso8601(currentTime));
        
        List<Announcement> expiredAnnouncements = announcementMapper.findExpiredScheduledAnnouncements(currentTime);
        
        log.info("查询到{}个到期的待发布公告", expiredAnnouncements.size());
        return expiredAnnouncements;
    }

    /**
     * 批量发布到期的公告
     * 绕过AutoFill机制，手动更新status字段
     */
    @Transactional
    public int batchPublishExpiredAnnouncements(List<Long> announcementIds) {
        if (announcementIds == null || announcementIds.isEmpty()) {
            log.info("没有需要发布的公告");
            return 0;
        }

        log.info("Manager层批量发布公告，数量：{}, ID列表：{}", announcementIds.size(), announcementIds);
        
        int successCount = 0;
        int failCount = 0;
        
        for (Long id : announcementIds) {
            try {
                int result = announcementMapper.publishAnnouncementManually(id);
                if (result > 0) {
                    successCount++;
                    log.info("定时发布公告成功，ID：{}", id);
                } else {
                    failCount++;
                    log.warn("定时发布公告失败，ID：{}，可能已被删除或状态已改变", id);
                }
            } catch (Exception e) {
                failCount++;
                log.error("定时发布公告异常，ID：{}", id, e);
            }
        }
        
        log.info("批量发布完成，成功：{}个，失败：{}个", successCount, failCount);
        return successCount;
    }
}

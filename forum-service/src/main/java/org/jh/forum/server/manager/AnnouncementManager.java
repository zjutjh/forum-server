package org.jh.forum.server.manager;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.entity.Announcement.AnnouncementStatus;
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
     * 格式化为ISO-8601 格式字符串（用于响应）
     * 注意：数据库存储的是 UTC+8 本地时间，直接格式化输出
     */
    public String formatToIso8601(LocalDateTime dateTime) {
        if (dateTime == null)
        {return null;}
        // 直接格式化，不做时区转换，因为数据库存储的就是 UTC+8 时间
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    /**
     * 格式化 ZonedDateTime 为 ISO-8601 格式字符串
     * 用于处理 scheduledAt 等带时区的时间字段
     */
    public String formatZonedDateTimeToIso8601(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null)
            return null;
        // 直接使用 ZonedDateTime 的 ISO 格式化
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
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
    @Transactional
    public AnnouncementOperationResponse editAnnouncement(Long id, EditAnnouncementRequest request) {
        log.info("Manager层执行数据库更新操作，ID：{}，标题：{}", id, request.getTitle());

        // 构建要更新的实体对象
        Announcement updateEntity = Announcement.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .scheduledAt(request.getScheduledAt())
                .status(Announcement.AnnouncementStatus.fromCode(request.getStatus()))
                .attribute(convertAttributeToString(request.getAttribute()))
                .sticky(request.getSticky() != null ? request.getSticky() : false)
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
                .status(Announcement.AnnouncementStatus.fromCode(request.getStatus()))
                .scheduledAt(request.getScheduledAt())
                .attribute(convertAttributeToString(request.getAttribute()))
                .sticky(request.getSticky() != null ? request.getSticky() : false)
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
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title) {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper
        return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getTitle, title)
                .eq(Announcement::getDeleted, false));
    }

    /**
     * 检查标题是否重复（编辑时使用，排除当前公告ID，已排除软删除，后如无特殊情况不再注明）
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title, Long excludeId) {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper
        return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getTitle, title)
                .ne(Announcement::getId, excludeId)
                .eq(Announcement::getDeleted, false));
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
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkExist(Long id) {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper
        return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, id)
                .eq(Announcement::getDeleted, false));
    }

    /**
     * 根据ID获取公告实体（用于业务逻辑，返回实体对象）
     * 使用 MyBatis-Plus 的 selectOne 方法优化
     */
    public Announcement getAnnouncementEntityById(Long id) {
        log.info("Manager层查询公告实体，ID：{}", id);
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper
        return announcementMapper.selectOne(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, id)
                .eq(Announcement::getDeleted, false));
    }

    /**
     * 根据ID查询公告
     */
    public AnnouncementDetailsResponse getAnnouncementById(Long id) {
        log.info("Manager层查询公告详情，ID：{}", id);

        // 使用 MyBatis-Plus 查询公告实体
        Announcement raw = getAnnouncementEntityById(id);

        if (raw == null) {
            throw new RuntimeException("公告不存在或已被删除");
        }

        AnnouncementDetailsResponse response = new AnnouncementDetailsResponse();
        response.setId(id);
        response.setTitle(raw.getTitle());
        response.setContent(raw.getContent());
        response.setType(raw.getType());
        // 设置状态码为枚举的code值
        if (raw.getStatus() != null) {
            response.setStatus(raw.getStatus().getCode());
        } else {
            response.setStatus(null);
        }
        response.setCreator(getUsernameById(raw.getCreateUid()));
        response.setUpdator(getUsernameById(raw.getUpdateUid()));

        // 使用实际的时间数据和时间数据
        response.setCreatedAt(formatToIso8601(raw.getCreatedAt()));
        response.setUpdatedAt(formatToIso8601(raw.getUpdatedAt()));
        response.setScheduledAt(formatZonedDateTimeToIso8601(raw.getScheduledAt()));
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

        // 使用 MyBatis-Plus 查询公告实体
        Announcement raw = getAnnouncementEntityById(id);

        if (raw == null) {
            throw new IllegalArgumentException("公告不存在或已被删除");
        }else if(raw.getStatus() != AnnouncementStatus.PUBLISHED){
            throw new IllegalArgumentException("公告状态异常");
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
     * 分页查询公告列表（用户版本）
     * 用户查询需要特殊的排序逻辑（置顶优先），暂时保留专用方法
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

        // 用户查询需要特殊排序（置顶优先），继续使用专用 Mapper 方法
        List<Announcement> announcements = announcementMapper.findAnnouncementsForUser(
                request.getType(),
                offset,
                size);

        // 查询总数可以使用统一方法
        Long totalCount = announcementMapper.countAnnouncementsForUser(
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
        if (announcement.getStatus() != null) {
            item.setStatus(announcement.getStatus().getCode());
        } else {
            item.setStatus(null);
        }   
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
     * 使用 MyBatis-Plus 的 selectCount 方法优化
     * 
     * @param excludeId 排除的公告ID（用于编辑时检查）
     * @return true表示可以置顶，false表示已达上限
     */
    public boolean canStickyAnnouncement(Long excludeId) {
        long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getSticky, true)
                .ne(Announcement::getId, excludeId)
                .eq(Announcement::getDeleted, false));
        return count < 3; // 最多允许3个置顶公告
    }

    /**
     * 检查是否可以置顶公告（新增公告时使用）
     * 使用 MyBatis-Plus 的 selectCount 方法优化
     * 
     * @return true表示可以置顶，false表示已达上限，方法重载
     */
    public boolean canStickyAnnouncement() {
        long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getSticky, true)
                .eq(Announcement::getDeleted, false));
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
     * 使用 QueryWrapper 动态构建查询条件，替代多个固定的 Mapper 方法
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

        // 使用 QueryWrapper 构建动态查询条件
        List<Announcement> announcements = findAnnouncementsWithConditions(
            request.getStatus(), 
            null, // type 在管理员查询中不筛选
            includeDeleted, 
            getOrderFieldByStatus(request.getStatus()),
            orderType == 1, // true=降序，false=升序
            offset, 
            size
        );

        // 查询总数
        Long totalCount = countAnnouncementsWithConditions(
            request.getStatus(), 
            null, 
            includeDeleted
        );

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
     * 将 Announcement 实体转换为管理员版 AnnouncementItemResponse
     * 包含所有字段，包括 status, created_at, scheduled_at 等管理员需要的信息
     */
    private ListAnnouncementResponse.AnnouncementItemResponse convertToAdminAnnouncementItem(
            Announcement announcement) {
        ListAnnouncementResponse.AnnouncementItemResponse item = new ListAnnouncementResponse.AnnouncementItemResponse();

        item.setId(announcement.getId());
        item.setTitle(announcement.getTitle());
        item.setType(announcement.getType());
        // 管理员版包含状态字段
        if (announcement.getStatus() != null) {
            item.setStatus(announcement.getStatus().getCode());
        } else {
            item.setStatus(null);
        } 
        
        item.setSticky(announcement.getSticky());

        // 设置用户名
        item.setCreator(getUsernameById(announcement.getCreateUid()));
        item.setUpdator(getUsernameById(announcement.getUpdateUid()));

        // 格式化时间 - 管理员版包含完整时间信息
        item.setCreatedAt(formatToIso8601(announcement.getCreatedAt()));
        item.setUpdatedAt(formatToIso8601(announcement.getUpdatedAt()));

        // 预定发布时间 - 可能为null
        if (announcement.getScheduledAt() != null) {
            item.setScheduledAt(formatZonedDateTimeToIso8601(announcement.getScheduledAt()));
        }

        return item;
    }

    /**
     * 查询到期的待发布公告
     * 用于定时发布功能，使用 MyBatis-Plus 优化
     */
    public List<Announcement> findExpiredScheduledAnnouncements() {
        LocalDateTime currentTime = LocalDateTime.now();
        log.info("Manager层查询到期的待发布公告，当前时间：{}", formatToIso8601(currentTime));

        // 使用 MyBatis-Plus 查询到期的待发布公告
        List<Announcement> expiredAnnouncements = announcementMapper.selectList(
            new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 2) // 待发布状态
                .le(Announcement::getScheduledAt, currentTime) // 预定时间 <= 当前时间
                .eq(Announcement::getDeleted, false)
                .orderByAsc(Announcement::getScheduledAt) // 按预定时间升序
        );

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
    
    /**
     * 通用查询方法 - 使用 QueryWrapper 动态构建条件
     * 替代 Mapper 中的多个固定查询方法
     * 
     * @param status 公告状态（必填）
     * @param type 公告类型（可选筛选）
     * @param includeDeleted 是否包含已删除数据
     * @param orderField 排序字段
     * @param isDesc 是否降序
     * @param offset 分页偏移量
     * @param limit 每页记录数
     * @return 符合条件的公告列表
     */
    public List<Announcement> findAnnouncementsWithConditions(
            Integer status, 
            Integer type,
            Boolean includeDeleted, 
            String orderField,
            Boolean isDesc,
            Integer offset, 
            Integer limit) {
        
        log.debug("使用 QueryWrapper 查询公告，状态：{}，类型：{}，包含删除：{}，排序：{} {}",
                status, type, includeDeleted, orderField, isDesc ? "DESC" : "ASC");
        
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        
        // 动态添加查询条件
        queryWrapper.eq(status != null, Announcement::getStatus, status)
                   .eq(type != null, Announcement::getType, type)
                   .eq(!Boolean.TRUE.equals(includeDeleted), Announcement::getDeleted, false);
        
        // 动态排序 - 根据状态选择不同的排序字段
        if ("scheduled_at".equals(orderField)) {
            queryWrapper.orderBy(true, !Boolean.TRUE.equals(isDesc), Announcement::getScheduledAt);
        } else {
            // 默认按 updated_at 排序
            queryWrapper.orderBy(true, !Boolean.TRUE.equals(isDesc), Announcement::getUpdatedAt);
        }
        
        // 分页处理 - 使用 MyBatis-Plus 的 last() 方法
        queryWrapper.last("LIMIT " + offset + ", " + limit);
        
        return announcementMapper.selectList(queryWrapper);
    }
    
    /**
     * 通用计数方法 - 使用 QueryWrapper 动态构建条件
     * 
     * @param status 公告状态（必填）
     * @param type 公告类型（可选筛选）
     * @param includeDeleted 是否包含已删除数据
     * @return 符合条件的公告总数
     */
    public Long countAnnouncementsWithConditions(Integer status, Integer type, Boolean includeDeleted) {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(status != null, Announcement::getStatus, status)
                   .eq(type != null, Announcement::getType, type)
                   .eq(!Boolean.TRUE.equals(includeDeleted), Announcement::getDeleted, false);
        
        return announcementMapper.selectCount(queryWrapper);
    }
    
    /**
     * 根据公告状态获取对应的排序字段
     * 
     * @param status 公告状态
     * @return 排序字段名
     */
    private String getOrderFieldByStatus(Integer status) {
        if (status != null && status == 2) {
            // 待发布状态按 scheduled_at 排序
            return "scheduled_at";
        }
        // 其他状态（草稿、已发布）按 updated_at 排序
        return "updated_at";
    }
 
    /**
     * 检查指定用户是否为公告的创建者
     * 
     * @param announcementId 公告ID
     * @param userId 用户ID
     * @return 是否为创建者
     */
    public boolean isAnnouncementCreator(Long announcementId, Long userId) {
        if (announcementId == null || userId == null) {
            return false;
        }
        
        try {
            Announcement announcement = announcementMapper.selectById(announcementId);
            if (announcement == null || announcement.getDeleted()) {
                return false;
            }
            return userId.equals(announcement.getCreateUid());
        } catch (Exception e) {
            log.error("检查公告创建者权限失败，公告ID: {}, 用户ID: {}", announcementId, userId, e);
            return false;
        }
    }

    /**
     * 获取公告的创建者ID
     * 
     * @param announcementId 公告ID
     * @return 创建者ID，如果公告不存在返回null
     */
    public Long getAnnouncementCreatorId(Long announcementId) {
        if (announcementId == null) {
            return null;
        }
        
        try {
            Announcement announcement = announcementMapper.selectById(announcementId);
            if (announcement == null || announcement.getDeleted()) {
                return null;
            }
            return announcement.getCreateUid();
        } catch (Exception e) {
            log.error("获取公告创建者ID失败，公告ID: {}", announcementId, e);
            return null;
        }
    }
}
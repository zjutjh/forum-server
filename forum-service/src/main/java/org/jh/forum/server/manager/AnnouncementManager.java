package org.jh.forum.server.manager;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;

import org.apache.dubbo.common.utils.StringUtils;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;

import org.jh.forum.common.dto.response.AnnouncementOperationResponse;

import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.common.filters.MarkdownMathJaxHtmlFilter;
import org.jh.forum.server.mapper.AnnouncementMapper;
import org.jh.forum.server.utils.CacheUtil;
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
     * 注意: 数据库存储的是 UTC+8 本地时间, 直接格式化输出
     */
    public String formatToIso8601(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        // 直接格式化, 不做时区转换, 因为数据库存储的就是 UTC+8 时间
        try {
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        } catch (Exception e) {
            log.warn("时间转换失败, {}", e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 格式化 ZonedDateTime 为 ISO-8601 格式字符串
     * 用于处理带时区的时间字段
     */
    public String formatZonedDateTimeToIso8601(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        // 直接使用 ZonedDateTime 的 ISO 格式化
        try {
            return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (Exception e) {
            log.warn("时间转换失败, {}", e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }

    private final MarkdownMathJaxHtmlFilter filter = new MarkdownMathJaxHtmlFilter();

    /**
     * 防止标题和内容SQL、XSS注入(理论上来说应该没这个问题，但是谁知道呢)
     */
    private void safeFilter(CreateAnnouncementRequest request) {
        request.setTitle(filter.filterTitle(request.getTitle()));
        request.setContent(filter.filterContent(request.getContent()));
    }

    private void safeFilter(EditAnnouncementRequest request) {
        request.setTitle(filter.filterTitle(request.getTitle()));
        request.setContent(filter.filterContent(request.getContent()));
    }

    private String safeFilterTitle(String tile) {
        return filter.filterTitle(tile);
    }

    /**
     * 创建公告 - 原子数据库操作
     */
    @Transactional(rollbackFor = Exception.class)
    public Announcement createAnnouncement(CreateAnnouncementRequest request, ZonedDateTime publishedAt) {
        log.info("Manager层执行数据库插入操作, 公告标题: {}", request.getTitle());
        try {
            safeFilter(request);

            Announcement newEntity = Announcement.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(AnnouncementTypeEnum.fromCode(request.getType()))
                    .scheduledAt(request.getScheduledAt())
                    .publishedAt(publishedAt)
                    .status(AnnouncementStatusEnum.fromCode(request.getStatus()))
                    .attribute(convertAttributeToString(request.getAttribute()))
                    .sticky(request.getSticky() != null ? request.getSticky() : false)
                    .build();
            announcementMapper.insert(newEntity);
            log.info("数据库插入成功,  announcement_id: {}", newEntity.getId());
            return newEntity;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库插入公告异常, 标题:{}, 异常:{}", request.getTitle(), e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 编辑基础字段 - 原子数据库操作（已发布公告只能编辑这些字段）
     * 允许编辑: title, content, type, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementOperationResponse editBasicFields(Long id, EditAnnouncementRequest request) {
        log.info("Manager层执行基础字段更新操作, ID: {}, 标题: {}", id, request.getTitle());

        try {
            // 使用 MyBatis-Plus 的 updateById 方法, 会自动触发 AutoFillHandler
            safeFilter(request);
            Announcement updateEntity = Announcement.builder()
                    .id(id)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(AnnouncementTypeEnum.fromCode(request.getType()))
                    .attribute(convertAttributeToString(request.getAttribute()))
                    .sticky(request.getSticky())
                    .build();

            announcementMapper.updateById(updateEntity);

            log.info("基础字段更新成功, ID: {}", id);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库修改公告失败, id:{}, 异常: {}", request.getId(), e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 编辑所有字段 - 原子数据库操作（草稿和待发布公告可以编辑所有字段）
     * 允许编辑: title, content, type, status, scheduled_at, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementOperationResponse editAllFields(Long id, EditAnnouncementRequest request,
            ZonedDateTime publishedAt) {
        log.info("Manager层执行所有字段更新操作, ID: {}, 标题: {}", id, request.getTitle());

        try {
            // 使用 MyBatis-Plus 的 updateById 方法, 会自动触发 AutoFillHandler
            safeFilter(request);
            Announcement updateEntity = Announcement.builder()
                    .id(id)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(AnnouncementTypeEnum.fromCode(request.getType()))
                    .status(AnnouncementStatusEnum.fromCode(request.getStatus()))
                    .scheduledAt(request.getScheduledAt())
                    .publishedAt(publishedAt)
                    .attribute(convertAttributeToString(request.getAttribute()))
                    .sticky(request.getSticky() != null ? request.getSticky() : false)
                    .build();

            announcementMapper.updateById(updateEntity);

            log.info("所有字段更新成功, ID: {}", id);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库修改公告失败, id:{}, 异常: {}", request.getId(), e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 删除公告 - 原子数据库操作（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementOperationResponse deleteAnnouncement(Long id) {
        log.info("Manager层执行数据库软删除操作, ID: {}", id);

        try {

            announcementMapper.deleteById(id);

            log.info("数据库软删除成功, ID: {}", id);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库删除公告失败, id:{}, 异常: {}", id, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 检查标题是否重复（创建时使用,已排除软删除公告）
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title) {
        try {
            return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getTitle, safeFilterTitle(title))
                    .eq(Announcement::getDeleted, false));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("标题查重失败, 标题: {}, 异常: {}", title, e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    /**
     * 检查标题是否重复（编辑时使用, 排除当前公告ID, 已排除软删除, 后如无特殊情况不再注明）
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title, Long excludeId) {
        try {
            return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getTitle, safeFilterTitle(title))
                    .ne(Announcement::getId, excludeId)
                    .eq(Announcement::getDeleted, false));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("标题查重失败, 标题: {}, 异常: {}", title, e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    /**
     * 将Object类型的attribute转换为String
     * 如果是null则返回null, 如果是String则直接返回, 否则转换为JSON字符串
     */
    private String convertAttributeToString(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            if (attribute instanceof String) {
                return (String) attribute;
            }
            // 对于其他类型, 转换为JSON字符串, 这里先简单返回toString()
            return attribute.toString();
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("attribute转为json失败: 原文: {} 错误: {}", attribute, e);
            throw new IllegalArgumentException(ExceptionEnum.JSON_PARSE_ERROR.getErrorMsg(), e);
        }
    }

    /**
     * 根据ID检查公告是否存在
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkExist(Long id) {
        log.debug("Manager层查询公告实体存在性, ID: {}", id);
        try {
            // 使用 MyBatis-Plus 的 LambdaQueryWrapper
            return !announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getId, id)
                    .eq(Announcement::getDeleted, false));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("确定存在失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    /**
     * 根据ID获取公告实体（用于业务逻辑, 返回实体对象）(下面两个方法的基础)
     * 使用 MyBatis-Plus 的 selectOne 方法优化
     */
    public Announcement getAnnouncementEntityById(Long id) {
        log.debug("Manager层查询公告实体, ID: {}", id);
        try {
            // 使用 MyBatis-Plus 的 LambdaQueryWrapper
            return announcementMapper.selectOne(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getId, id)
                    .eq(Announcement::getDeleted, false));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("获取失败: id: {},{}", id, e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    /**
     * 根据ID查询公告（管理员版）
     */
    public AnnouncementDetailResponse getAnnouncementById(Long id) {
        log.debug("Manager层查询公告详情, ID: {}", id);
        try {
            // 使用 MyBatis-Plus 查询公告实体
            Announcement raw = getAnnouncementEntityById(id);

            if (raw == null) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            AnnouncementDetailResponse response = new AnnouncementDetailResponse();
            response.setId(id);
            response.setTitle(raw.getTitle());
            response.setContent(raw.getContent());
            response.setType(raw.getType());
            response.setStatus(raw.getStatus());
            response.setCreator(cacheUtil.getUsernameById(raw.getCreateUid()));
            response.setUpdator(cacheUtil.getUsernameById(raw.getUpdateUid()));

            // 使用实际的时间数据和时间数据
            response.setCreatedAt(formatToIso8601(raw.getCreatedAt()));
            response.setUpdatedAt(formatToIso8601(raw.getUpdatedAt()));
            response.setScheduledAt(formatZonedDateTimeToIso8601(raw.getScheduledAt()));
            response.setAttribute(raw.getAttribute());
            response.setSticky(raw.getSticky());

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Manager-公告转基本内容失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }

    }

    /**
     * 根据ID查询公告详情（用户版本）
     * 返回简化的公告信息, 不包含管理员才需要的字段
     */
    public AnnouncementTinyDetailsResponse getAnnouncementTinyDetailsById(Long id) {
        log.debug("Manager-用户版查询公告详情, ID: {}", id);

        try {
            // 使用 MyBatis-Plus 查询公告实体
            Announcement raw = getAnnouncementEntityById(id);

            if (raw == null) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            } else if (raw.getStatus() != AnnouncementStatusEnum.PUBLISHED
                    && (raw.getStatus() != AnnouncementStatusEnum.SCHEDULED
                            && (raw.getScheduledAt() == null || raw.getScheduledAt().isBefore(ZonedDateTime.now())))) {
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
            response.setCreator(cacheUtil.getUsernameById(raw.getCreateUid()));
            response.setUpdator(cacheUtil.getUsernameById(raw.getUpdateUid()));

            // 格式化时间
            response.setCreatedAt(formatToIso8601(raw.getCreatedAt()));
            response.setUpdatedAt(formatToIso8601(raw.getUpdatedAt()));

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Manager-公告转基本内容失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * id-username：直接调用 cacheUtil的
     */
    @Resource
    private CacheUtil cacheUtil;

    /**
     * 通用分页查询结果包装类
     */
    public static class PageResult<T> {
        private List<T> items;
        private Long total;

        public PageResult(List<T> items, Long total) {
            this.items = items;
            this.total = total;
        }

        public List<T> getItems() {
            return items;
        }

        public Long getTotal() {
            return total;
        }
    }

    /**
     * 用户分页查询公告 - 只返回实体数据
     */
    public PageResult<Announcement> findUserAnnouncementsWithPaging(
            UserQueryAnnouncementRequest request) {

        log.debug("Manager层用户查询公告列表, 页码: {}, 类型: {}",
                request.getPage(), request.getType());

        try {
            // 计算分页参数
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getPageSize() != null ? request.getPageSize() : 8;
            int offset = (page - 1) * size;

            // 查询数据和总数
            List<Announcement> announcements = findUserAnnouncements(
                    request.getType(), offset, size, request.getKeywords());
            Long totalCount = countUserAnnouncements(request.getType(), request.getKeywords());

            return new PageResult<>(announcements, totalCount);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户分页查询公告失败, request: {}, 异常: {}", request, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 管理员分页查询公告 - 只返回实体数据
     */
    public PageResult<Announcement> findAdminAnnouncementsWithPaging(
            AdminQueryAnnouncementRequest request) {

        log.debug("Manager层管理员查询公告列表, 页码: {}, 状态: {}, 类型: {}",
                request.getPage(), request.getStatus(), request.getType());

        try {
            // 计算分页参数
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getPageSize() != null ? request.getPageSize() : 8;
            int offset = (page - 1) * size;
            boolean isDesc = request.orderType() == 1;
            boolean includeDeleted = request.getDeleted() != null ? request.getDeleted() : false;

            // 查询数据和总数
            List<Announcement> announcements = findAdminAnnouncements(
                    request.getStatus(), request.getType(), includeDeleted,
                    isDesc, request.getKeywords(), offset, size);
            Long totalCount = countAdminAnnouncements(
                    request.getStatus(), request.getType(), includeDeleted, request.getKeywords());

            return new PageResult<>(announcements, totalCount);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员分页查询公告失败, request: {}, 异常: {}", request, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 检查是否可以置顶公告（检查置顶数量限制）
     * 使用 MyBatis-Plus 的 selectCount 方法优化
     * 最多允许3个置顶公告
     * 
     * @param excludeId 排除的公告ID（用于编辑时检查）
     * @return true表示可以置顶, false表示已达上限
     */
    public boolean canStickyAnnouncement(Long excludeId) {
        try {
            long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getSticky, true)
                    .ne(Announcement::getId, excludeId)
                    .eq(Announcement::getDeleted, false));
            return count >= 3;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("检查置顶公告失败, 异常: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    /**
     * 检查是否可以置顶公告（新增公告时使用）
     * 使用 MyBatis-Plus 的 selectCount 方法优化
     * 最多允许3个置顶公告
     * 
     * @return true表示可以置顶, false表示已达上限, 方法重载
     */
    public boolean canStickyAnnouncement() {
        try {
            long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getSticky, true)
                    .eq(Announcement::getDeleted, false));
            return count < 3;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("检查置顶公告失败, 异常: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean isSticky) {
        log.info("Manager层置顶/取消置顶公告, ID: {}, 置顶状态: {}", id, isSticky);

        try {
            // 构建要更新的实体对象（只更新sticky字段）
            Announcement updateEntity = Announcement.builder()
                    .id(id)
                    .sticky(isSticky)
                    .build();

            // 使用MyBatis-Plus的updateById方法, 会自动触发AutoFillHandler更新update_uid和updated_at
            int result = announcementMapper.updateById(updateEntity);

            if (result <= 0) {
                throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
            }

            log.info("置顶状态更新成功, ID: {}, sticky: {}", id, isSticky);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据库更新公告置顶状态失败, id:{}, 异常: {}", id, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 查询到期的待发布公告
     * 用于定时发布功能, 使用 MyBatis-Plus 优化
     */
    public List<Announcement> findExpiredScheduledAnnouncements() {
        LocalDateTime currentTime = LocalDateTime.now();
        log.info("Manager层查询到期的待发布公告, 当前时间: {}", formatToIso8601(currentTime));

        try {
            // 使用 MyBatis-Plus 查询到期的待发布公告
            List<Announcement> expiredAnnouncements = announcementMapper.selectList(
                    new LambdaQueryWrapper<Announcement>()
                            // 待发布状态
                            .eq(Announcement::getStatus, 2)
                            // 预定时间 <= 当前时间
                            .le(Announcement::getScheduledAt, currentTime)
                            // 是否包括被删除的
                            .eq(Announcement::getDeleted, false)
                            // 按预定时间升序
                            .orderByAsc(Announcement::getScheduledAt));

            log.info("查询到{}个到期的待发布公告", expiredAnnouncements.size());
            return expiredAnnouncements;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询到期待发布公告失败, 异常: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 批量发布到期的公告
     * 绕过AutoFill机制避免update_uid被覆盖, 手动更新status字段
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchPublishExpiredAnnouncements(List<Long> announcementIds) {
        if (announcementIds == null || announcementIds.isEmpty()) {
            log.info("没有需要发布的公告");
            return 0;
        }

        log.info("Manager层批量发布公告, 数量: {}, ID列表: {}", announcementIds.size(), announcementIds);

        int successCount = 0;
        int failCount = 0;

        try {
            for (Long id : announcementIds) {
                try {
                    int result = announcementMapper.publishAnnouncementManually(id);
                    if (result > 0) {
                        successCount++;
                        log.info("定时发布公告成功, ID: {}", id);
                    } else {
                        failCount++;
                        log.warn("定时发布公告失败, ID: {}, 可能已被删除或状态已改变", id);
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("定时发布公告异常, ID: {}", id, e);
                }
            }
            log.info("批量发布完成, 成功: {}个, 失败: {}个", successCount, failCount);
            return successCount;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量发布到期公告异常, ID列表: {}, 异常: {}", announcementIds, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 通用查询条件构建器
     * 
     * @param isAdmin        是否为管理员查询
     * @param status         状态筛选（管理员专用）
     * @param type           类型筛选
     * @param includeDeleted 是否包含已删除（管理员专用）
     * @param keywords       关键词搜索
     */
    private LambdaQueryWrapper<Announcement> buildQueryConditions(boolean isAdmin, String status, String type,
            Boolean includeDeleted, String keywords) {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();

        if (isAdmin) {
            // 管理员查询条件
            queryWrapper
                    .eq(status != null && !"all".equals(status), Announcement::getStatus, status)
                    .eq(type != null && !"all".equals(type), Announcement::getType, type)
                    .like(StringUtils.isNotBlank(keywords), Announcement::getTitle, keywords)
                    .eq(!Boolean.TRUE.equals(includeDeleted), Announcement::getDeleted, false);
        } else {
            // 用户查询条件
            queryWrapper.eq(Announcement::getDeleted, false);

            // 状态条件：已发布 OR (定时发布且时间已到)
            queryWrapper.and(wrapper -> wrapper
                    .eq(Announcement::getStatus, "published")
                    .or(subWrapper -> subWrapper
                            .eq(Announcement::getStatus, "scheduled")
                            .le(Announcement::getScheduledAt, LocalDateTime.now())));

            // 类型筛选（可选）
            queryWrapper.eq(type != null && !"all".equals(type), Announcement::getType, type);
            // 关键词筛选（可选）
            queryWrapper.like(StringUtils.isNotBlank(keywords), Announcement::getTitle, keywords);
        }

        return queryWrapper;
    }

    /**
     * 通用分页查询模板
     * 
     * @param isAdmin        是否为管理员查询
     * @param status         状态筛选
     * @param type           类型筛选
     * @param includeDeleted 是否包含已删除
     * @param keywords       关键词搜索
     * @param isDesc         是否降序(默认状态: 最近的在最上面)
     * @param offset         偏移量
     * @param limit          限制数量
     */
    private List<Announcement> queryAnnouncements(boolean isAdmin, String status, String type,
            Boolean includeDeleted, String keywords, Boolean isDesc, Integer offset, Integer limit) {

        try {
            LambdaQueryWrapper<Announcement> queryWrapper = buildQueryConditions(isAdmin, status, type, includeDeleted,
                    keywords);

            if (isAdmin) {
                // 管理员排序：根据状态选择不同的排序字段
                if (AnnouncementStatusEnum.SCHEDULED.getCode().equals(status)) {
                    queryWrapper.orderByDesc(Announcement::getSticky);
                    queryWrapper.orderBy(true, isDesc, Announcement::getScheduledAt);
                } else if (AnnouncementStatusEnum.PUBLISHED.getCode().equals(status)) {
                    queryWrapper.orderByDesc(Announcement::getSticky);
                    queryWrapper.orderBy(true, isDesc, Announcement::getPublishedAt);
                } else {
                    queryWrapper.orderBy(true, isDesc, Announcement::getUpdatedAt);
                }
            } else {
                // 用户排序：按发布时间升序
                queryWrapper.orderByAsc(Announcement::getPublishedAt);
            }

            // 分页
            queryWrapper.last("LIMIT " + offset + ", " + limit);

            return announcementMapper.selectList(queryWrapper);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询公告失败, isAdmin: {}, status: {}, type: {}, 异常: {}", isAdmin, status, type, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 通用计数查询模板
     */
    private Long countAnnouncements(boolean isAdmin, String status, String type,
            Boolean includeDeleted, String keywords) {
        try {
            LambdaQueryWrapper<Announcement> queryWrapper = buildQueryConditions(isAdmin, status, type, includeDeleted,
                    keywords);
            return announcementMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("统计公告失败, isAdmin: {}, status: {}, type: {}, 异常: {}", isAdmin, status, type, e);
            throw new ForumServiceException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 用户公告查询
     */
    public List<Announcement> findUserAnnouncements(String type, Integer offset, Integer limit, String keywords) {
        log.debug("用户查询公告, 类型: {}, offset: {}, limit: {}", type, offset, limit);
        return queryAnnouncements(false, null, type, null, keywords, null, offset, limit);
    }

    /**
     * 用户公告计数
     */
    public Long countUserAnnouncements(String type, String keywords) {
        log.debug("用户统计公告数量, 类型: {}", type);
        return countAnnouncements(false, null, type, null, keywords);
    }

    /**
     * 管理员公告查询
     */
    public List<Announcement> findAdminAnnouncements(String status, String type, Boolean includeDeleted,
            Boolean isDesc, String keywords, Integer offset, Integer limit) {
        log.debug("管理员查询公告, 状态: {}, 类型: {}, 包含删除: {}, 降序: {}, 关键词: {}",
                status, type, includeDeleted, isDesc, keywords);
        return queryAnnouncements(true, status, type, includeDeleted, keywords, isDesc, offset, limit);
    }

    /**
     * 管理员公告计数
     */
    public Long countAdminAnnouncements(String status, String type, Boolean includeDeleted, String keywords) {
        log.debug("管理员统计公告数量, 状态: {}, 类型: {}, 包含删除: {}, 关键词: {}", status, type, includeDeleted, keywords);
        return countAnnouncements(true, status, type, includeDeleted, keywords);
    }

    /*
     * 获取置顶公告
     */
    public List<Announcement> getStickyAnnouncements() {
        log.debug("查询置顶公告");
        try {
            return new LambdaQueryChainWrapper<>(announcementMapper)
                    .eq(Announcement::getSticky, true)
                    .eq(Announcement::getDeleted, false)
                    .orderByDesc(Announcement::getUpdatedAt)
                    .last("LIMIT 3")
                    .list();
        } catch (Exception e) {
            log.error("查询置顶公告失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    /**
     * 获取最近更新的3个未被置顶的公告
     */
    public List<Announcement> getRecentAnnouncements() {
        log.debug("查询最近更新的3个未置顶公告");
        try {
            return new LambdaQueryChainWrapper<>(announcementMapper)
                    .eq(Announcement::getSticky, false)
                    .eq(Announcement::getDeleted, false)
                    .orderByDesc(Announcement::getUpdatedAt)
                    .last("LIMIT 3")
                    .list();
        } catch (Exception e) {
            log.error("查询最近更新的公告失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.DATABASE_ERROR);
        }
    }

    // ====================以下部分为辅助方法====================//

    /**
     * 检查指定用户是否为公告的创建者
     * 
     * @param announcementId 公告ID
     * @param userId         用户ID
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
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("检查公告创建者权限失败, 公告ID: {}, 用户ID: {}", announcementId, userId, e);
            return false;
        }
    }

    /**
     * 获取公告的创建者ID
     * 
     * @param announcementId 公告ID
     * @return 创建者ID, 如果公告不存在返回null
     */
    @Deprecated
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
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取公告创建者ID失败, 公告ID: {}", announcementId, e);
            return null;
        }
    }
}

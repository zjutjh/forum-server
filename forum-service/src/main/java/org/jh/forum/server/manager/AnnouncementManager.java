package org.jh.forum.server.manager;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.apache.dubbo.common.utils.StringUtils;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;
import org.jh.forum.common.dto.response.ListAnnouncementTinyResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.entity.Announcement.AnnouncementStatus;
import org.jh.forum.common.entity.Announcement.AnnouncementType;
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

    /**
     * 创建公告 - 原子数据库操作
     */
    @Transactional(rollbackFor = Exception.class)
    public Announcement createAnnouncement(CreateAnnouncementRequest request) {
        log.info("Manager层执行数据库插入操作, 公告标题: {}", request.getTitle());
        try {
            safeFilter(request);
            Announcement newEntity = Announcement.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(AnnouncementType.fromCode(request.getType()))
                    .scheduledAt(request.getScheduledAt())
                    .status(AnnouncementStatus.fromCode(request.getStatus()))
                    .attribute(convertAttributeToString(request.getAttribute()))
                    .sticky(request.getSticky() != null ? request.getSticky() : false)
                    .build();
            announcementMapper.insert(newEntity);
            log.info("数据库插入成功,  announcement_id: {}", newEntity.getId());
            return newEntity;
        } catch (Exception e) {
            log.error("数据库插入公告异常, 标题:{}, 异常:{}", request.getTitle(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_OPERATION_ERROR);
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
                    .type(Announcement.AnnouncementType.fromCode(request.getType()))
                    .attribute(convertAttributeToString(request.getAttribute()))
                    .sticky(request.getSticky())
                    .build();

            announcementMapper.updateById(updateEntity);

            log.info("基础字段更新成功, ID: {}", id);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (Exception e) {
            log.error("数据库修改公告失败, id:{}, 异常: {}", request.getId(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_OPERATION_ERROR);
        }
    }

    /**
     * 编辑所有字段 - 原子数据库操作（草稿和待发布公告可以编辑所有字段）
     * 允许编辑: title, content, type, status, scheduled_at, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    @Transactional(rollbackFor = Exception.class)
    public AnnouncementOperationResponse editAllFields(Long id, EditAnnouncementRequest request) {
        log.info("Manager层执行所有字段更新操作, ID: {}, 标题: {}", id, request.getTitle());

        try {
            // 使用 MyBatis-Plus 的 updateById 方法, 会自动触发 AutoFillHandler
            safeFilter(request);
            Announcement updateEntity = Announcement.builder()
                    .id(id)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(Announcement.AnnouncementType.fromCode(request.getType()))
                    .status(Announcement.AnnouncementStatus.fromCode(request.getStatus()))
                    .scheduledAt(request.getScheduledAt())
                    .attribute(convertAttributeToString(request.getAttribute()))
                    .sticky(request.getSticky() != null ? request.getSticky() : false)
                    .build();

            announcementMapper.updateById(updateEntity);

            log.info("所有字段更新成功, ID: {}", id);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (Exception e) {
            log.error("数据库修改公告失败, id:{}, 异常: {}", request.getId(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_OPERATION_ERROR);
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
        } catch (Exception e) {
            log.error("数据库删除公告失败, id:{}, 异常: {}", id, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_OPERATION_ERROR);
        }
    }

    /**
     * 检查标题是否重复（创建时使用,已排除软删除公告）
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title) {
        try {
            return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                    .eq(Announcement::getTitle, title)
                    .eq(Announcement::getDeleted, false));
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
                    .eq(Announcement::getTitle, title)
                    .ne(Announcement::getId, excludeId)
                    .eq(Announcement::getDeleted, false));
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
            // 对于其他类型, 可以转换为JSON字符串
            // 这里先简单返回toString(), 如果需要完整的JSON序列化可以使用Jackson
            return attribute.toString();
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
            response.setType(raw.getType().getCode());
            // 设置状态码为枚举的code值
            if (raw.getStatus() != null) {
                response.setStatus(raw.getStatus().getCode());
            } else {
                response.setStatus(0);
            }
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
            // 业务校验异常直接抛出，交给Service层处理
            throw e;
        } catch (Exception e) {
            log.warn("Manager-公告转基本内容失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_DETAIL_ERROR);
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
            } else if (raw.getStatus() != AnnouncementStatus.PUBLISHED
                    && (raw.getStatus() != AnnouncementStatus.SCHEDULED
                            && (raw.getScheduledAt() == null || raw.getScheduledAt().isBefore(ZonedDateTime.now())))) {
                throw new IllegalArgumentException("公告状态异常");
            }

            // 只返回用户需要的字段
            AnnouncementTinyDetailsResponse response = new AnnouncementTinyDetailsResponse();
            response.setId(id);
            response.setTitle(raw.getTitle());
            response.setContent(raw.getContent());
            response.setType(raw.getType().getCode());
            response.setSticky(raw.getSticky());

            // 填充用户信息
            response.setCreator(cacheUtil.getUsernameById(raw.getCreateUid()));
            response.setUpdator(cacheUtil.getUsernameById(raw.getUpdateUid()));

            // 格式化时间
            response.setCreatedAt(formatToIso8601(raw.getCreatedAt()));
            response.setUpdatedAt(formatToIso8601(raw.getUpdatedAt()));

            return response;
        } catch (IllegalArgumentException e) {
            // 业务校验异常直接抛出，交给Service层处理
            throw e;
        } catch (Exception e) {
            log.warn("Manager-公告转基本内容失败: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_DETAIL_ERROR);
        }
    }

    /**
     * id-username：直接调用 cacheUtil的
     */
    @Resource
    private CacheUtil cacheUtil;

    /**
     * 分页查询公告列表（用户版本）
     * 用户查询需要特殊的排序逻辑（置顶优先）, 暂时保留专用方法
     */
    public ListAnnouncementTinyResponse userListAnnouncements(
            UserQueryAnnouncementRequest request) {

        log.debug(
                "Manager层查询公告列表, 页码: {}, 类型: {}",
                request.getPage(),
                request.getType());
        try {
            // 计算分页参数
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getSize() != null ? request.getSize() : 8;
            int offset = (page - 1) * size;

            // 用户查询需要特殊排序（置顶优先）, 使用专用 Mapper 方法
            List<Announcement> announcements = announcementMapper.findAnnouncementsForUser(
                    request.getType(),
                    offset,
                    size);

            // 查询总数
            Long totalCount = announcementMapper.countAnnouncementsForUser(
                    request.getType());

            // 【批量优化】1. 收集所有需要的用户ID
            Set<Long> userIds = announcements.stream()
                    .flatMap(announcement -> Stream.of(
                            announcement.getCreateUid(),
                            announcement.getUpdateUid()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 【批量优化】2. 批量获取用户昵称
            Map<Long, String> nicknameMap = cacheUtil.getUsernamesByIds(userIds);

            // 【批量优化】3. 转换为响应对象（使用批量查询的结果）
            List<ListAnnouncementTinyResponse.AnnouncementItemResponse> itemList = announcements.stream()
                    .map(announcement -> convertToUserAnnouncementItemBatch(announcement, nicknameMap))
                    .toList();

            ListAnnouncementTinyResponse response = new ListAnnouncementTinyResponse();
            response.setTotal(totalCount.intValue());
            response.setPage(page);
            response.setPageSize(size);
            response.setList(itemList);

            log.debug("查询公告列表成功, 总数: {}, 当前页数据: {}", totalCount, itemList.size());
            return response;
        } catch (Exception e) {
            log.error("分页查询公告列表失败, request: {}, 异常: {}", request, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
        }
    }

    /**
     * 将 Announcement 实体转换为用户版 AnnouncementItemResponse(批量优化版)
     */
    private ListAnnouncementTinyResponse.AnnouncementItemResponse convertToUserAnnouncementItemBatch(
            Announcement announcement, Map<Long, String> nicknameMap) {

        try {
            ListAnnouncementTinyResponse.AnnouncementItemResponse item = new ListAnnouncementTinyResponse.AnnouncementItemResponse();
            item.setId(announcement.getId());
            item.setTitle(announcement.getTitle());
            item.setType(announcement.getType().getCode());
            item.setSticky(announcement.getSticky());

            // 【批量优化】设置用户名
            item.setCreator(nicknameMap.get(announcement.getCreateUid()));
            item.setUpdator(nicknameMap.get(announcement.getUpdateUid()));

            // 格式化时间
            // item.setCreatedAt(formatToIso8601(announcement.getCreatedAt()));
            item.setUpdatedAt(formatToIso8601(announcement.getUpdatedAt()));

            return item;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户版实体到列表失败, 异常: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
        }
    }

    /**
     * 管理员查询公告列表
     * 支持三种状态筛选（草稿、已发布、待发布）+ 升序/降序排序 + 已删除数据查询
     * 使用 QueryWrapper 动态构建查询条件, 替代多个固定的 Mapper 方法
     */
    public ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request) {
        log.debug("Manager层管理员查询公告列表, 页码: {}, 状态: {}, 类型: {}, 排序方向: {}, 查询已删除: {}",
                request.getPage(), request.getStatus(), request.getType(), request.orderType(), request.getDeleted());

        try {
            // 计算分页参数
            int page = request.getPage() != null ? request.getPage() : 1;
            int size = request.getSize() != null ? request.getSize() : 8;
            int offset = (page - 1) * size;
            int orderType = request.orderType();
            // 已有默认值处理
            boolean includeDeleted = request.getDeleted() != null ? request.getDeleted() : false;

            // 使用 QueryWrapper 构建动态查询条件
            List<Announcement> announcements = findAnnouncementsWithConditions(
                    request.getStatus(),
                    request.getType(),
                    includeDeleted,
                    getOrderFieldByStatus(request.getStatus()),
                    orderType == 1,
                    // true=降序, false=升序
                    offset,
                    size,
                    request.getKeyword());
            // DTO里是keywords, 毕竟查询的是标题里的keywords

            // 查询总数
            Long totalCount = countAnnouncementsWithConditions(
                    request.getStatus(),
                    request.getType(),
                    includeDeleted,
                    request.getKeyword());

            // 【批量优化】收集用户ID并批量查询
            Set<Long> userIds = announcements.stream()
                    .flatMap(announcement -> Stream.of(
                            announcement.getCreateUid(),
                            announcement.getUpdateUid()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Map<Long, String> nicknameMap = cacheUtil.getUsernamesByIds(userIds);

            // 转换为响应对象（管理员版本, 包含完整字段）
            List<ListAnnouncementResponse.AnnouncementItemResponse> itemList = announcements.stream()
                    .map(announcement -> convertToAdminAnnouncementItemBatch(announcement, nicknameMap))
                    .toList();

            // 构建响应
            ListAnnouncementResponse response = new ListAnnouncementResponse();
            response.setTotal(totalCount.intValue());
            response.setPage(page);
            response.setPageSize(size);
            response.setList(itemList);

            log.info("管理员查询公告列表完成, 总数: {}, 当前页数据: {}", totalCount, itemList.size());
            return response;
        } catch (Exception e) {
            log.error("管理员查询公告列表失败, request: {}, 异常: {}", request, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
        }
    }

    /**
     * 将 Announcement 实体转换为管理员版 AnnouncementItemResponse
     * 包含所有字段, 包括 status, created_at, scheduled_at 等管理员需要的信息
     */
    private ListAnnouncementResponse.AnnouncementItemResponse convertToAdminAnnouncementItemBatch(
            Announcement announcement, Map<Long, String> nicknameMap) {
        try {
            ListAnnouncementResponse.AnnouncementItemResponse item = new ListAnnouncementResponse.AnnouncementItemResponse();

            item.setId(announcement.getId());
            item.setTitle(announcement.getTitle());
            item.setType(announcement.getType().getCode());
            item.setStatus(announcement.getStatus().getCode());

            item.setSticky(announcement.getSticky());

            // 【批量优化】从Map获取用户昵称
            item.setCreator(nicknameMap.get(announcement.getCreateUid()));
            item.setUpdator(nicknameMap.get(announcement.getUpdateUid()));

            // 格式化时间 - 管理员版包含完整时间信息
            item.setCreatedAt(formatToIso8601(announcement.getCreatedAt()));
            item.setUpdatedAt(formatToIso8601(announcement.getUpdatedAt()));

            // 预定发布时间 - 可能为null
            if (announcement.getScheduledAt() != null) {
                item.setScheduledAt(formatZonedDateTimeToIso8601(announcement.getScheduledAt()));
            }

            return item;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("全部实体到列表失败, 异常: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
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
                throw new ForumServiceException("数据库更新失败, 数据库或公告状态异常");
            }

            log.info("置顶状态更新成功, ID: {}, sticky: {}", id, isSticky);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(id);

            return response;
        } catch (Exception e) {
            log.error("数据库更新公告置顶状态失败, id:{}, 异常: {}", id, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_OPERATION_ERROR);
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
                            .eq(Announcement::getDeleted, false)
                            // 按预定时间升序
                            .orderByAsc(Announcement::getScheduledAt));

            log.info("查询到{}个到期的待发布公告", expiredAnnouncements.size());
            return expiredAnnouncements;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询到期待发布公告失败, 异常: {}", e.getMessage(), e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
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
        } catch (Exception e) {
            log.error("批量发布到期公告异常, ID列表: {}, 异常: {}", announcementIds, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_OPERATION_ERROR);
        }
    }

    /**
     * 通用查询方法 - 使用 QueryWrapper 动态构建条件
     * 替代 Mapper 中的多个固定查询方法
     * 
     * @param status         公告状态（可选）
     * @param type           公告类型（可选筛选）
     * @param includeDeleted 是否包含已删除数据
     * @param orderField     排序字段
     * @param isDesc         是否降序
     * @param offset         分页偏移量
     * @param limit          每页记录数
     * @return 符合条件的公告列表
     */
    public List<Announcement> findAnnouncementsWithConditions(
            Integer status,
            Integer type,
            Boolean includeDeleted,
            String orderField,
            Boolean isDesc,
            Integer offset,
            Integer limit,
            String title) {

        log.debug("使用 QueryWrapper 查询公告, 状态: {}, 类型: {}, 包含删除: {}, 排序: {} {}",
                status, type, includeDeleted, orderField, isDesc ? "DESC" : "ASC");

        try {
            LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();

            // 动态添加查询条件
            queryWrapper
                    .eq(status != null && status != 3, Announcement::getStatus, status)
                    .eq(type != null && type != 3, Announcement::getType, type)
                    .like(StringUtils.isNotBlank(title), Announcement::getTitle, title)
                    .eq(!Boolean.TRUE.equals(includeDeleted), Announcement::getDeleted, false);

            // 动态排序 - 根据状态选择不同的排序字段
            if ("scheduled_at".equals(orderField)) {
                queryWrapper.orderBy(true, !isDesc, Announcement::getScheduledAt);
            } else {
                // 默认按 updated_at 排序
                queryWrapper.orderBy(true, !isDesc, Announcement::getUpdatedAt);
            }

            // 分页处理 - 使用 MyBatis-Plus 的 last() 方法
            queryWrapper.last("LIMIT " + offset + ", " + limit);

            return announcementMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("通用条件查询公告失败, status: {}, type: {}, 异常: {}", status, type, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
        }
    }

    /**
     * 通用计数方法 - 使用 QueryWrapper 动态构建条件
     * 
     * @param status         公告状态（可选，3=全部状态）
     * @param type           公告类型（可选筛选）
     * @param includeDeleted 是否包含已删除数据
     * @param title          标题关键词（可选，模糊检索）
     * @return 符合条件的公告总数
     */
    public Long countAnnouncementsWithConditions(Integer status, Integer type, Boolean includeDeleted, String title) {
        try {
            LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();

            // 动态添加查询条件 - 保持与列表查询完全一致
            queryWrapper
                    .eq(status != null && status != 3, Announcement::getStatus, status)
                    .eq(type != null && type != 3, Announcement::getType, type)
                    .eq(!Boolean.TRUE.equals(includeDeleted), Announcement::getDeleted, false)
                    .like(StringUtils.isNotBlank(title), Announcement::getTitle, title);

            return announcementMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("通用条件计数公告失败, status: {}, type: {}, title: {}, 异常: {}", status, type, title, e);
            throw new ForumServiceException(ExceptionEnum.ANNOUNCEMENT_LIST_QUERY_ERROR);
        }
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
        } catch (Exception e) {
            log.error("获取公告创建者ID失败, 公告ID: {}", announcementId, e);
            return null;
        }
    }
}

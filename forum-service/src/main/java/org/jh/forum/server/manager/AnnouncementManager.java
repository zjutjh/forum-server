package org.jh.forum.server.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.jh.forum.common.constants.AnnouncementStatusEnum;
import org.jh.forum.common.constants.AnnouncementTypeEnum;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.server.mapper.AnnouncementMapper;
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
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final int DEFAULT_PAGE = 1;
    @Resource
    private AnnouncementMapper announcementMapper;

    /**
     * 创建公告 - 原子数据库操作
     */
    public Announcement createAnnouncement(CreateAnnouncementRequest request, LocalDateTime publishedAt) {
        log.debug("执行数据库插入操作, 公告标题: {}", request.getTitle());
        Announcement newEntity = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(AnnouncementTypeEnum.fromCode(request.getType()))
                .scheduledAt(request.getScheduledAt())
                .publishedAt(publishedAt)
                .status(AnnouncementStatusEnum.fromCode(request.getStatus()))
                .attribute(request.getAttribute())
                .sticky(request.getSticky() != null ? request.getSticky() : false)
                .build();
        announcementMapper.insert(newEntity);
        log.info("数据库插入成功,  announcement_id: {}", newEntity.getId());
        return newEntity;
    }

    /**
     * 编辑基础字段 - 原子数据库操作（已发布公告只能编辑这些字段）
     * 允许编辑: title, content, type, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    public AnnouncementOperationResponse editBasicFields(Long id, EditAnnouncementRequest request) {
        log.debug("执行基础字段更新操作, ID: {}, 标题: {}", id, request.getTitle());

        // 使用 MyBatis-Plus 的 updateById 方法, 会自动触发 AutoFillHandler
        Announcement updateEntity = Announcement.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .type(AnnouncementTypeEnum.fromCode(request.getType()))
                .attribute((request.getAttribute()))
                .sticky(request.getSticky())
                .build();

        announcementMapper.updateById(updateEntity);
        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnouncementId(id);
        log.info("基础字段更新成功, ID: {}", id);

        return response;
    }

    /**
     * 编辑所有字段 - 原子数据库操作（草稿和待发布公告可以编辑所有字段）
     * 允许编辑: title, content, type, status, scheduled_at, attribute, sticky
     * update_uid 和 updated_at 由 AutoFillHandler 自动填充
     */
    public AnnouncementOperationResponse editAllFields(Long id, EditAnnouncementRequest request,
                                                       LocalDateTime publishedAt) {
        log.debug("执行所有字段更新操作, ID: {}, 标题: {}", id, request.getTitle());

        Announcement updateEntity = Announcement.builder()
                .id(id)
                .title(request.getTitle())
                .content(request.getContent())
                .type(AnnouncementTypeEnum.fromCode(request.getType()))
                .status(AnnouncementStatusEnum.fromCode(request.getStatus()))
                .scheduledAt(request.getScheduledAt())
                .publishedAt(publishedAt)
                .attribute((request.getAttribute()))
                .sticky(request.getSticky() != null ? request.getSticky() : false)
                .build();

        announcementMapper.updateById(updateEntity);

        log.info("所有字段更新成功, ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnouncementId(id);

        return response;
    }

    /**
     * 删除公告 - 原子数据库操作（软删除）
     */
    public AnnouncementOperationResponse deleteAnnouncement(Long id) {
        log.debug("执行数据库软删除操作, ID: {}", id);

        announcementMapper.deleteById(id);

        log.info("数据库软删除成功, ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnouncementId(id);

        return response;
    }


    /**
     * 根据ID获取公告实体（用于业务逻辑, 返回实体对象）
     * 使用 MyBatis-Plus 的 selectOne 方法
     */
    public Announcement getAnnouncementEntityById(Long id) {
        log.debug("查询公告实体, ID: {}", id);
        return announcementMapper.selectOne(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, id));
    }


    /**
     * 用户分页查询公告列表
     */
    public IPage<Announcement> findUserAnnouncementsWithPaging(
            UserQueryAnnouncementRequest request) {

        log.debug("用户查询公告列表, 页码: {}, 类型: {}",
                request.getPage(), request.getType());

        // 创建分页对象
        int page = request.getPage() != null ? request.getPage() : DEFAULT_PAGE;
        int size = request.getPageSize() != null ? request.getPageSize() : DEFAULT_PAGE_SIZE;
        IPage<Announcement> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<Announcement> queryWrapper = buildQueryConditions(
                false, AnnouncementStatusEnum.PUBLISHED.getCode(), request.getType(), false, request.getKeywords());

        // 用户排序：置顶优先，然后按发布时间降序（最近发布的在最上面）
        queryWrapper.orderByDesc(Announcement::getSticky)
                .orderByDesc(Announcement::getPublishedAt);

        // 执行分页查询
        return announcementMapper.selectPage(pageParam, queryWrapper);
    }

    /**
     * 管理员分页查询公告列表
     */
    public IPage<Announcement> findAdminAnnouncementsWithPaging(
            AdminQueryAnnouncementRequest request) {

        log.debug("管理员查询公告列表, 页码: {}, 状态: {}, 类型: {}",
                request.getPage(), request.getStatus(), request.getType());

        // 创建分页对象
        int page = request.getPage() != null ? request.getPage() : DEFAULT_PAGE;
        int size = request.getPageSize() != null ? request.getPageSize() : DEFAULT_PAGE_SIZE;
        IPage<Announcement> pageParam = new Page<>(page, size);

        // 构建查询条件
        boolean includeDeleted = request.getDeleted() != null ? request.getDeleted() : false;
        LambdaQueryWrapper<Announcement> queryWrapper = buildQueryConditions(
                true, request.getStatus(), request.getType(), includeDeleted, request.getKeywords());

        // 管理员排序：根据状态选择不同的排序字段
        boolean isDesc = request.orderType() == 1;
        if (AnnouncementStatusEnum.SCHEDULED.getCode().equals(request.getStatus())) {
            queryWrapper.orderByDesc(Announcement::getSticky);
            queryWrapper.orderBy(true, isDesc, Announcement::getScheduledAt);
        } else if (AnnouncementStatusEnum.PUBLISHED.getCode().equals(request.getStatus())) {
            queryWrapper.orderByDesc(Announcement::getSticky);
            queryWrapper.orderBy(true, isDesc, Announcement::getPublishedAt);
        } else {
            queryWrapper.orderBy(true, isDesc, Announcement::getUpdatedAt);
        }

        // 执行分页查询
        return announcementMapper.selectPage(pageParam, queryWrapper);
    }


    public AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean isSticky) {
        log.debug("置顶/取消置顶公告, ID: {}, 置顶状态: {}", id, isSticky);

        Announcement updateEntity = Announcement.builder()
                .id(id)
                .sticky(isSticky)
                .build();

        // 使用MyBatis-Plus的updateById方法
        int result = announcementMapper.updateById(updateEntity);

        if (result <= 0) {
            throw new ApiException(ExceptionEnum.SERVER_ERROR);
        }

        log.info("置顶状态更新成功, ID: {}, sticky: {}", id, isSticky);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnouncementId(id);

        return response;
    }

    /**
     * 查询到期的待发布公告
     * 用于定时发布功能, 使用 MyBatis-Plus 优化
     */
    public List<Announcement> findExpiredScheduledAnnouncements() {
        LocalDateTime currentTime = LocalDateTime.now();
        log.debug("查询到期的待发布公告, 当前时间: {}", currentTime);

        List<Announcement> expiredAnnouncements = announcementMapper.selectList(
                new LambdaQueryWrapper<Announcement>()
                        // 待发布状态
                        .eq(Announcement::getStatus, AnnouncementStatusEnum.SCHEDULED.getCode())
                        // 预定时间 <= 当前时间
                        .le(Announcement::getScheduledAt, currentTime)
                        // 是否包括被删除的
                        .eq(Announcement::getDeleted, false)
                        // 按预定时间升序
                        .orderByAsc(Announcement::getScheduledAt));

        log.info("查询到{}个到期的待发布公告", expiredAnnouncements.size());
        return expiredAnnouncements;
    }

    /**
     * 批量发布到期的公告
     * 绕过AutoFill机制避免update_uid被覆盖, 手动更新status字段
     */

    public int batchPublishExpiredAnnouncements(List<Long> announcementIds) {
        if (announcementIds == null || announcementIds.isEmpty()) {
            log.info("没有需要发布的公告");
            return 0;
        }

        log.info("批量发布公告, 数量: {}, ID列表: {}", announcementIds.size(), announcementIds);

        int successCount = 0;
        int failCount = 0;

        for (Long id : announcementIds) {
            int result = announcementMapper.publishAnnouncementManually(id);
            if (result > 0) {
                successCount++;
                log.debug("定时发布公告成功, ID: {}", id);
            } else {
                failCount++;
                log.warn("定时发布公告失败, ID: {}, 可能已被删除或状态已改变", id);
            }
        }
        log.info("批量发布完成, 成功: {}个, 失败: {}个", successCount, failCount);
        return successCount;
    }

    /**
     * 获取置顶公告
     */
    public List<Announcement> getStickyAnnouncements() {
        log.debug("查询置顶公告");
        return new LambdaQueryChainWrapper<>(announcementMapper)
                .eq(Announcement::getSticky, true)
                .eq(Announcement::getDeleted, false)
                .orderByDesc(Announcement::getUpdatedAt)
                .last("LIMIT 3")
                .list();
    }

    /**
     * 获取最近更新的3个未被置顶的公告
     */
    public List<Announcement> getRecentAnnouncements() {
        log.debug("查询最近更新的3个未置顶公告");
        return new LambdaQueryChainWrapper<>(announcementMapper)
                .eq(Announcement::getSticky, false)
                .eq(Announcement::getDeleted, false)
                .orderByDesc(Announcement::getUpdatedAt)
                .last("LIMIT 3")
                .list();
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

        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null || announcement.getDeleted()) {
            return false;
        }
        return userId.equals(announcement.getCreateUid());
    }

    // ====================以下部分为辅助方法====================//

    /**
     * 检查标题是否重复（创建时使用,已排除软删除公告）
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title) {
        return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getTitle, title)
                .eq(Announcement::getDeleted, false));
    }

    /**
     * 检查标题是否重复（编辑时使用, 排除当前公告ID, 已排除软删除, 后如无特殊情况不再注明）
     * 使用 MyBatis-Plus 的 exists 方法优化
     */
    public boolean checkTitleDuplicate(String title, Long excludeId) {
        return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getTitle, title)
                .ne(Announcement::getId, excludeId)
                .eq(Announcement::getDeleted, false));
    }


    /**
     * 根据ID检查公告是否存在（MyBatis-Plus 的 exists 方法）
     * 存在时返回true，不存在false， 目前基本都是反转引用
     */
    public boolean isExist(Long id) {
        log.debug("查询公告实体存在性, ID: {}", id);
        return announcementMapper.exists(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, id)
                .eq(Announcement::getDeleted, false));
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
        long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getSticky, true)
                .ne(Announcement::getId, excludeId)
                .eq(Announcement::getDeleted, false));
        return count < 3;
    }

    /**
     * 检查是否可以置顶公告（新增公告时使用）
     * 使用 MyBatis-Plus 的 selectCount 方法
     * 最多允许3个置顶公告
     *
     * @return true表示可以置顶, false表示已达上限, 方法重载
     */
    public boolean canStickyAnnouncement() {
        long count = announcementMapper.selectCount(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getSticky, true)
                .eq(Announcement::getDeleted, false));
        return count < 3;
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

        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null || announcement.getDeleted()) {
            return null;
        }
        return announcement.getCreateUid();
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
}

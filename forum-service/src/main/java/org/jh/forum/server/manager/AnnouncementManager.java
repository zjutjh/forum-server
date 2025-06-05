package org.jh.forum.server.manager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.entity.mapper.AnnouncementMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.util.StrUtil;
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
     */
    public String formatToIso8601(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime
                .atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

    /**
     * 创建公告
     */
    @Transactional
    public Announcement createAnnouncement(Announcement entity) {
        log.info("Manager层创建公告：{}", entity.getTitle());

        // 校验标题和内容
        validateTitleAndContent(entity.getTitle(), entity.getContent());

        // 标题查重校验（使用trim后的标题）
        String trimmedTitle = StrUtil.trim(entity.getTitle());
        if (checkTitleDuplicate(trimmedTitle)) {
            throw new IllegalArgumentException("公告标题已存在，请使用其他标题");
        }

        // 校验公告类型
        validateAnnouncementType(entity.getType());

        // 校验定时发布和状态逻辑
        validateCreateScheduleAndStatus(
                entity.getScheduledAt(),
                entity.getStatus());
        int result = announcementMapper.insert(entity);
        if (result > 0) {
            log.info("created, announcement_id: {}", entity.getId());
            return entity;
        } else {
            throw new RuntimeException("Failed to insert announcement");
        }
    }

    /**
     * 校验标题和内容
     */
    private void validateTitleAndContent(String title, String content) {
        // 校验标题长度（2-50字符，空字符串长度为0，自动覆盖非空校验）
        String trimmedTitle = StrUtil.trim(title);
        if (trimmedTitle == null ||
                trimmedTitle.length() < 2 ||
                trimmedTitle.length() > 50) {
            throw new IllegalArgumentException(
                    "公告标题长度必须在2-50字符之间");
        } // PS: 标题这里空字符的手动校验是用来保底的，这边基本上只会处理掉50字符以上的情况

        // 校验内容长度（2-500字符，空字符串长度为0，自动覆盖非空校验）
        String trimmedContent = StrUtil.trim(content);
        if (trimmedContent == null ||
                trimmedContent.length() < 2 ||
                trimmedContent.length() > 500) {
            throw new IllegalArgumentException(
                    "公告内容长度必须在2-500字符之间");
        }
    }

    /**
     * 校验公告类型
     */
    private void validateAnnouncementType(int type) {
        if (type != 0 && type != 1) {
            throw new IllegalArgumentException("公告类型无效");
        }
    }

    /**
     * 校验创建时的定时发布和状态逻辑
     */
    private void validateCreateScheduleAndStatus(
            LocalDateTime scheduledAt,
            Integer status) {
        if (scheduledAt != null) {
            // scheduled_at非空时，必须是未来时间（+30秒保底）
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
            LocalDateTime minAllowedTime = now.plusSeconds(30);

            if (scheduledAt.isBefore(minAllowedTime)) {
                throw new IllegalArgumentException(
                        "定时发布时间必须必须至少在当前时间30秒之后");
            }

            // scheduled_at非空时，status只能为2
            if (status == null || status != 2) {
                throw new IllegalArgumentException(
                        "已设置定时发布，状态已锁定");
            }
        } else {
            // scheduled_at为空时，status可以为0或1
            if (status != null && status != 0 && status != 1) {
                throw new IllegalArgumentException(
                        "未设置定时发布时，状态只能为草稿或已发布");
            }
        }
    }

    /**
     * 校验编辑时的权限和逻辑
     */
    private void validateEditPermissions(
            Integer currentStatus,
            EditAnnouncementRequest request) {
        if (currentStatus == null) {
            throw new IllegalArgumentException("无法获取公告当前状态");
        }
        if (currentStatus == 1) {
            // status为1（已发布）的公告，只允许编辑title、content、attribute
            // 不允许修改scheduled_at和status

            if (request.getScheduledAt() != null) {
                throw new IllegalArgumentException(
                        "已发布的公告不允许修改定时发布时间");
            }

            if (request.getStatus() != null && request.getStatus() != 1) {
                throw new IllegalArgumentException(
                        "已发布的公告不允许修改发布状态");
            }
        } else if (currentStatus == 0 || currentStatus == 2) {
            // status为0（草稿）或2（待发布）的公告，可以编辑所有字段，包括scheduled_at //
            // 如果修改了scheduled_at或status，需要校验时间和状态逻辑

            // 直接使用LocalDateTime进行验证
            LocalDateTime scheduledAt = request.getScheduledAt();
            Integer newStatus = request.getStatus();

            // 如果请求中包含scheduled_at或status字段，进行相应验证
            if (scheduledAt != null || (newStatus != null && newStatus == 2)) {
                // 当设置scheduled_at或status为2时，应用创建时的验证逻辑
                validateCreateScheduleAndStatus(scheduledAt, newStatus);
            } else if (newStatus != null && scheduledAt == null) {
                // 如果只设置status但不设置scheduled_at，验证status的合法性
                if (newStatus != 0 && newStatus != 1 && newStatus != 2) {
                    throw new IllegalArgumentException(
                            "状态值无效，只能为草稿、已发布或待发布");
                }

                // 如果设置status为2但没有scheduled_at，这是不合法的
                if (newStatus == 2) {
                    throw new IllegalArgumentException(
                            "设置状态为待发布时必须指定定时发布时间");
                }
            }
        } else {
            throw new IllegalArgumentException("公告状态异常，无法编辑");
        }
    }

    /**
     * 检查标题是否重复（创建时使用）
     */
    private boolean checkTitleDuplicate(String title) {
        return announcementMapper.existsByTitle(title);
    }

    /**
     * 检查标题是否重复（编辑时使用，排除当前公告ID）
     */
    private boolean checkTitleDuplicate(String title, Integer excludeId) {
        return announcementMapper.existsByTitleAndIdNot(title, excludeId);
    }

    /**
     * 根据ID查询公告
     */
    public AnnouncementDetailsResponse getAnnouncementById(Integer id) {
        log.info("Manager层查询公告详情，ID：{}", id);

        if (id == null || id <= 0) {
            return null;
        }

        // TODO: 实际实现中这里会调用Mapper层从数据库查询
        // 现在先返回Mock数据

        AnnouncementDetailsResponse response = new AnnouncementDetailsResponse();
        response.setId(id);
        response.setTitle("Mock公告标题 - " + id);
        response.setContent("这是一个Mock公告内容，用于测试业务逻辑...");
        response.setType(0);
        response.setStatus(1);
        response.setCreator("admin");
        response.setUpdator("admin");
        response.setCreatedAt(
                formatToIso8601(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusDays(1)));
        response.setUpdatedAt(
                formatToIso8601(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusHours(2)));
        response.setScheduledAt(null);
        response.setAttribute("{\"sticky\": true}");

        return response;
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
                request.getType()); // TODO: 实际实现中这里会调用Mapper层分页查询数据库
        // 现在先返回Mock数据

        List<ListAnnouncementResponse.AnnouncementItemResponse> list = new ArrayList<>();

        for (int i = 1; i <= Math.min(request.getSize(), 8); i++) {
            ListAnnouncementResponse.AnnouncementItemResponse item = new ListAnnouncementResponse.AnnouncementItemResponse();
            item.setId(i + (request.getPage() - 1) * request.getSize());
            item.setTitle("Manager Mock公告 - " + item.getId());

            // 设置类型（0=系统公告，1=学校公告）
            if ("系统公告".equals(request.getType())) {
                item.setType(0); // 系统公告
            } else if ("学校公告".equals(request.getType())) {
                item.setType(1); // 学校公告
            } else {
                item.setType(0); // 默认为系统公告
            }

            // 设置状态（已发布=1）
            item.setStatus(1);
            item.setCreator("admin");
            item.setUpdator("admin");
            item.setCreatedAt(formatToIso8601(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusDays(i)));
            item.setUpdatedAt(formatToIso8601(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusHours(i)));
            list.add(item);
        }

        ListAnnouncementResponse response = new ListAnnouncementResponse();
        response.setTotal(88); // Mock总数
        response.setPage(request.getPage());
        response.setPageSize(request.getSize());
        response.setList(list);

        return response;
    }

    /**
     * 编辑公告
     */
    public AnnouncementOperationResponse editAnnouncement(
            Integer id,
            EditAnnouncementRequest request) {
        log.info("Manager层编辑公告，ID：{}，标题：{}", id, request.getTitle());

        if (id == null || id <= 0) {
            log.warn("公告ID无效: {}", id);
            return null;
        }

        // 校验标题和内容
        validateTitleAndContent(request.getTitle(), request.getContent());

        // 校验公告类型
        validateAnnouncementType(request.getType());

        // 标题查重校验（编辑时排除当前公告ID，使用trim后的标题）
        String trimmedTitle = StrUtil.trim(request.getTitle());
        if (checkTitleDuplicate(trimmedTitle, id)) {
            throw new IllegalArgumentException(
                    "公告标题已存在，请使用其他标题");
        }

        // 获取当前公告状态并校验编辑权限
        // TODO: 实际实现中需要从数据库查询真实的当前状态
        Integer currentStatus = getCurrentAnnouncementStatus(id);
        validateEditPermissions(currentStatus, request);

        // TODO: 实际实现中这里会调用Mapper层更新数据库记录
        // 现在先返回Mock结果

        log.info("公告编辑成功，ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 获取当前公告状态（Mock方法）
     * TODO: 实际实现中需要从数据库查询
     */
    private Integer getCurrentAnnouncementStatus(Integer id) {
        // Mock数据：假设不同ID对应不同状态
        if (id % 3 == 1) {
            return 1; // 已发布
        } else if (id % 3 == 2) {
            return 2; // 待发布
        } else {
            return 0; // 草稿
        }
    }

    /**
     * 删除公告
     */
    public AnnouncementOperationResponse deleteAnnouncement(Integer id) {
        log.info("Manager层删除公告，ID：{}", id);

        if (id == null || id <= 0) {
            return null;
        } // TODO: 实际实现中这里会调用Mapper层软删除数据库记录
          // 现在先返回Mock结果

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 置顶/取消置顶公告
     */
    public AnnouncementOperationResponse stickyAnnouncement(Integer id, Boolean isSticky) {
        log.info("Manager层置顶/取消置顶公告，ID：{}，置顶状态：{}", id, isSticky);

        if (id == null || id <= 0) {
            return null;
        }

        if (isSticky == null) {
            return null;
        }

        // TODO: 实际实现中这里会调用Mapper层更新数据库记录的attribute字段
        // 将置顶状态存储在attribute字段的JSON中，例如：{"sticky": true}
        // 现在先返回Mock结果

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 管理员查询公告列表
     * 支持复杂的筛选条件和排序
     */
    public ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request) {
        log.info(
                "Manager层管理员查询公告列表，页码：{}，排序字段：{}", request.getPage(), request.orderField()); // TODO:
                                                                                             // 实际实现中这里会调用Mapper层进行复杂查询
        // 现在先返回Mock数据，模拟管理员查询功能

        List<ListAnnouncementResponse.AnnouncementItemResponse> list = new ArrayList<>(); // 模拟根据筛选条件生成不同的数据

        // 构建模拟数据
        for (int i = 1; i <= Math.min(request.getSize(), 10); i++) {
            ListAnnouncementResponse.AnnouncementItemResponse item = new ListAnnouncementResponse.AnnouncementItemResponse();
            item.setId(i + (request.getPage() - 1) * request.getSize());
            item.setTitle("Admin Mock公告 - " + item.getId());
            item.setType(0);
            item.setStatus(1);
            item.setCreator("admin");
            item.setUpdator("suadmin");
            item.setCreatedAt(formatToIso8601(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusDays(i)));
            item.setUpdatedAt(formatToIso8601(LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusHours(i)));
            list.add(item);
        }

        // 构建响应
        ListAnnouncementResponse response = new ListAnnouncementResponse();
        response.setList(list);
        response.setTotal(35); // Mock总数
        response.setPage(request.getPage());
        response.setPageSize(request.getSize());

        log.info("管理员查询公告列表完成，返回{}条记录", list.size());
        return response;
    }
}

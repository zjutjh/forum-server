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
    public AnnouncementOperationResponse editAnnouncement(Integer id, EditAnnouncementRequest request) {
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
    public AnnouncementOperationResponse editBasicFields(Integer id, EditAnnouncementRequest request) {
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
    public AnnouncementOperationResponse editAllFields(Integer id, EditAnnouncementRequest request) {
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
    public AnnouncementOperationResponse deleteAnnouncement(Integer id) {
        log.info("Manager层执行数据库软删除操作，ID：{}", id);


        // 使用MyBatis-Plus的updateById方法进行软删除，会自动触发AutoFillHandler更新update_uid和updated_at
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
    public boolean checkTitleDuplicate(String title, Integer excludeId) {
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
    public boolean checkExist(Integer id) {
        return announcementMapper.checkExist(id);
    }

    /**
     * 根据ID获取公告实体（用于业务逻辑，返回实体对象）
     */
    public Announcement getAnnouncementEntityById(Integer id) {
        log.info("Manager层查询公告实体，ID：{}", id);
        return announcementMapper.selectById(id);
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
     * 检查是否可以置顶公告（检查置顶数量限制）
     * 
     * @param excludeId 排除的公告ID（用于编辑时检查）
     * @return true表示可以置顶，false表示已达上限
     */
    public boolean canStickyAnnouncement(Integer excludeId) {
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
    public AnnouncementOperationResponse stickyAnnouncement(Integer id, Boolean isSticky) {
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

package org.jh.forum.server.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.server.entity.Announcement;
import org.jh.forum.server.repository.AnnouncementRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告业务管理层
 * 负责处理公告相关的业务逻辑
 * @author SituChengxiang
 */
@Slf4j
@Component
public class AnnouncementManager {

    @Resource
    private AnnouncementRepository announcementRepository;

    /**
     * 创建公告
     */
    @Transactional
    public Announcement createAnnouncement(Announcement entity) {
        log.info("Manager层创建公告：{}", entity.getTitle());
        
        Announcement saved = announcementRepository.save(entity);
        log.info("公告创建成功，ID: {}", saved.getId());
        
        return saved;
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

        AnnouncementDetailsResponse response =
            new AnnouncementDetailsResponse();
        response.setId(id);
        response.setTitle("Mock公告标题 - " + id);
        response.setContent("这是一个Mock公告内容，用于测试业务逻辑...");
        response.setType("系统公告");
        response.setStatus(1);
        response.setStatusName("已发布");
        response.setCreatorId(123);
        response.setUpdatorId(123);
        response.setCreatedAt(LocalDateTime.now().minusDays(1));
        response.setUpdatedAt(LocalDateTime.now().minusHours(2));
        response.setScheduledAt(null);
        response.setDeleted(false);
        response.setAttribute("{\"sticky\": true}");

        return response;
    }

    /**
     * 分页查询公告列表
     */
    public ListAnnouncementResponse listAnnouncements(
        ListAnnouncementRequest request
    ) {
        log.info(
            "Manager层查询公告列表，页码：{}，状态：{}",
            request.getPage(),
            request.getStatus()
        );

        // TODO: 实际实现中这里会调用Mapper层分页查询数据库
        // 现在先返回Mock数据

        List<ListAnnouncementResponse.AnnouncementItemResponse> list =
            new ArrayList<>();

        for (int i = 1; i <= Math.min(request.getSize(), 8); i++) {
            ListAnnouncementResponse.AnnouncementItemResponse item =
                new ListAnnouncementResponse.AnnouncementItemResponse();
            item.setId(i + (request.getPage() - 1) * request.getSize());
            item.setTitle("Manager Mock公告 - " + item.getId());
            item.setType(i % 2 == 0 ? "系统公告" : "学校公告");
            Integer status = request.getStatus() != null
                ? request.getStatus()
                : 1;
            item.setStatus(status);
            item.setStatusName(getStatusName(status));
            item.setCreatorId(123);
            item.setCreatedAt(LocalDateTime.now().minusDays(i).toString());
            item.setUpdatedAt(LocalDateTime.now().minusHours(i).toString());
            list.add(item);
        }

        ListAnnouncementResponse response = new ListAnnouncementResponse();
        response.setTotal(88L); // Mock总数
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setPages((int) Math.ceil(88.0 / request.getSize()));
        response.setList(list);

        return response;
    }

    /**
     * 编辑公告
     */
    public AnnouncementOperationResponse editAnnouncement(
        Integer id,
        EditAnnouncementRequest request
    ) {
        log.info("Manager层编辑公告，ID：{}，标题：{}", id, request.getTitle());

        if (id == null || id <= 0) {
            log.warn("公告ID无效: {}", id);
            return null;
        }

        // TODO: 实际实现中这里会调用Mapper层更新数据库记录
        // 现在先返回Mock结果

        log.info("公告编辑成功，ID: {}", id);

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 删除公告
     */
    public AnnouncementOperationResponse deleteAnnouncement(Integer id) {
        log.info("Manager层删除公告，ID：{}", id);

        if (id == null || id <= 0) {
            return null;
        }

        // TODO: 实际实现中这里会调用Mapper层软删除数据库记录
        // 现在先返回Mock结果

        AnnouncementOperationResponse response = new AnnouncementOperationResponse();
        response.setAnnounceId(id);

        return response;
    }

    /**
     * 生成ID（Mock用）
     */
    private Integer generateId() {
        return (int) (System.currentTimeMillis() % 10000);
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已发布";
            case 2:
                return "待发布";
            default:
                return "未知";
        }
    }
}

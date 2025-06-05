package org.jh.forum.server.service;

import java.time.LocalDateTime;

import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.service.AnnouncementService;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.server.manager.AnnouncementManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告服务实现类
 * 
 * @author SituChengxiang（SK）
 */
@Slf4j
@Service
@DubboService
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementManager announcementManager;

    @Override
    public AnnouncementOperationResponse createAnnouncement(CreateAnnouncementRequest request) {
        try {
            // Service层负责DTO->Entity转换
            Announcement entity = convertToEntity(request);

            Announcement saved = announcementManager.createAnnouncement(entity);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(saved.getId());
            return response;
        } catch (Exception e) {
            // 包装数据库异常
            if (e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常
            throw e;
        }
    }

    // DTO转Entity的私有方法 这里缺少一点特殊字段驼峰-蛇形转换，现在先这么写着
    private Announcement convertToEntity(CreateAnnouncementRequest request) {
        // 直接使用LocalDateTime，无需时区转换
        LocalDateTime scheduledAt = request.getScheduledAt();
        if (scheduledAt != null) {
            log.debug("使用定时发布时间: {}", scheduledAt);
        }
        return Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .createUid(123l) // Mock创建人ID
                .updateUid(123L) // 创建时设置更新人为创建人
                .scheduledAt(scheduledAt).status(request.getStatus() != null ? request.getStatus() : 0)
                .deleted(false) // 新创建的公告默认未删除
                .attribute(convertAttributeToString(request.getAttribute()))
                .build();
    }

    @Override
    public AnnouncementDetailsResponse getAnnouncementById(Integer id) {
        log.info("Service层查询公告详情，ID：{}", id);
        return announcementManager.getAnnouncementById(id);
    }

    @Override
    public ListAnnouncementResponse listAnnouncements(ListAnnouncementRequest request) {
        log.info("Service层查询公告列表，页码：{}，状态：{}", request.getPage(), request.getStatus());
        return announcementManager.listAnnouncements(request);
    }

    @Override
    public AnnouncementOperationResponse editAnnouncement(Integer id, EditAnnouncementRequest request) {
        log.info("Service层编辑公告，ID：{}，标题：{}", id, request.getTitle());
        // 处理时间转换
        processEditRequestTime(request);
        return announcementManager.editAnnouncement(id, request);
    }

    @Override
    public AnnouncementOperationResponse deleteAnnouncement(Integer id) {
        log.info("Service层删除公告，ID：{}", id);
        return announcementManager.deleteAnnouncement(id);
    }

    @Override
    public AnnouncementOperationResponse stickyAnnouncement(Integer id, Boolean isSticky) {
        log.info("Service层置顶/取消置顶公告，ID：{}，置顶状态：{}", id, isSticky);
        return announcementManager.stickyAnnouncement(id, isSticky);
    }

    @Override
    public ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request) {
        // log.info("Service层管理员查询公告列表，页码：{}，筛选条件：{}", request.getPage(),
        // request.getFilters());
        // TODO: 这里先返回mock数据，后续实现真正的管理员查询逻辑
        return announcementManager.adminQueryAnnouncements(request);
    }

    /**
     * 直接使用数据库创建公告（用于测试Repository功能）
     * 
     * @param request 创建公告请求
     * @return 创建的公告操作响应
     */
    /**
     * 处理EditAnnouncementRequest中的时间转换（用于编辑功能）
     */
    private void processEditRequestTime(EditAnnouncementRequest request) {
        // 如果有定时发布时间，直接使用LocalDateTime，无需转换
        if (request.getScheduledAt() != null) {
            log.debug("编辑请求包含定时发布时间: {}", request.getScheduledAt());
        }
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
}
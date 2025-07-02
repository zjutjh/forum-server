package org.jh.forum.server.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.service.AnnouncementService;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.server.manager.AnnouncementManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告服务实现类
 * 
 * @author SituChengxiang (SK)
 */
@Slf4j
@Service
@DubboService
public class AnnouncementServiceImpl implements AnnouncementService {

    @Override
    public AnnouncementOperationResponse createAnnouncement(CreateAnnouncementRequest request) {
        try {

            // 标题和内容基本
            validateTitleAndContent(request.getTitle(), request.getContent());

            // 标题查重校验 (使用trim后的标题)
            String trimmedTitle = request.getTitle().trim();
            if (announcementManager.checkTitleDuplicate(trimmedTitle)) {
                throw new IllegalArgumentException("公告标题已存在, 请使用其他标题");
            }
            // 校验公告类型
            validateAnnouncementType(request.getType());

            // 校验定时发布和状态逻辑
            validateScheduledAndStatus(request.getScheduledAt(), request.getStatus());

            // 如果设置为置顶, 检查置顶公告数量限制 (最多3个)
            if (request.getSticky() && !announcementManager.canStickyAnnouncement()) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            // Manager层执行原子数据库操作
            Announcement saved = announcementManager.createAnnouncement(request);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(saved.getId());
            return response;
        } catch (IllegalArgumentException e) {
            // 包装参数错误
            log.warn("参数校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("创建公告-manager:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 别的异常
            log.error("创建公告未知异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    // 编辑公告

    @Override
    public AnnouncementOperationResponse editAnnouncement(Long id, EditAnnouncementRequest request) {
        try {
            // log.info("Service层编辑公告, ID:{}, 标题:{}", id, request.getTitle());

            // 统一存在性校验
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // 校验标题和内容
            validateTitleAndContent(request.getTitle(), request.getContent());

            // 标题查重校验 (编辑时排除当前公告ID)
            String trimmedTitle = request.getTitle().trim();
            if (announcementManager.checkTitleDuplicate(trimmedTitle, id)) {
                throw new IllegalArgumentException("公告标题已存在, 请使用其他标题");
            }

            // 校验公告类型
            validateAnnouncementType(request.getType());

            // 校验定时发布和状态逻辑
            validateScheduledAndStatus(request.getScheduledAt(), request.getStatus());

            Announcement originAnnouncement = announcementManager.getAnnouncementEntityById(id);

            // 如果设置为置顶, 检查置顶公告数量限制 (最多3个)
            if (request.getSticky() != null && request.getSticky()) {
                // 只有在希望置顶且当前公告未置顶时, 才检查数量限制
                if (!announcementManager.canStickyAnnouncement(id)) {
                    throw new IllegalArgumentException("置顶公告数量已达上限");
                }
            }

            // 内联权限状态检验
            if (originAnnouncement.getStatus() == Announcement.AnnouncementStatus.PUBLISHED) {
                // 如果当前公告为已发布状态, 则不允许编辑定时发布和状态
                if (request.getScheduledAt() != null || request.getScheduledAt() != originAnnouncement.getScheduledAt()
                        || request.getStatus() != 1) {
                    throw new IllegalArgumentException("已发布的公告不允许编辑定时发布和状态");
                }
                if (request.getStatus() != null && request.getStatus() != 1) {
                    throw new IllegalArgumentException("已发布的公告不允许修改发布状态");
                }
                // 执行基础字段更新 (只能编辑标题、内容、类型、属性、置顶)
                return announcementManager.editBasicFields(id, request);
            } else {
                // 草稿(0)和待发布(2)状态的公告可以编辑所有字段
                return announcementManager.editAllFields(id, request);
            }

        } catch (IllegalArgumentException e) {
            // 包装参数错误
            log.warn("参数校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("manager-编辑公告异常:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 别的异常
            log.error("编辑公告异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    @Resource
    private AnnouncementManager announcementManager;

    // 置顶/取消置顶公告
    @Override
    public AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean sticky) {
        try {
            log.info("Service层置顶/取消置顶公告, ID:{}, 置顶状态:{}", id, sticky);

            // 校验sticky参数 (防御性编程)
            if (sticky == null) {
                throw new IllegalArgumentException("置顶状态不能为空, 必须为true或false");
            }

            // 校验ID并检查公告是否存在且未被删除
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // 如果置顶, 检查置顶公告数量限制 (最多3个)
            if (sticky && !announcementManager.canStickyAnnouncement(id)) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            return announcementManager.stickyAnnouncement(id, sticky);

        } catch (IllegalArgumentException e) {
            // 包装参数错误
            log.warn("参数校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("manager-置顶/取消置顶失败:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 别的异常
            log.error("置顶/取消公告异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    // 删除公告
    @Override
    public AnnouncementOperationResponse deleteAnnouncement(Long id) {
        try {
            log.info("Service层删除公告, ID:{}", id);
            // 校验ID
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // 执行删除操作
            return announcementManager.deleteAnnouncement(id);
        } catch (IllegalArgumentException e) {
            // 包装参数错误
            log.warn("参数校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("编辑公告-manager:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 别的异常
            log.error("编辑公告异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    // 根据ID查询公告详情 (管理员)
    @Override
    public AnnouncementDetailsResponse getAnnouncementById(Long id) {
        try {
            log.info("Service层查询公告详情, ID:{}", id);

            // 校验ID
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // 具体查询操作
            return announcementManager.getAnnouncementById(id);
        } catch (IllegalArgumentException e) {
            // 包装校验异常为参数错误
            log.warn("查询公告详情校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("查询公告详情-manager:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 抛出其他异常
            log.error("查询公告详情未知异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    // 根据ID查询公告详情 (用户版本)
    @Override
    public AnnouncementTinyDetailsResponse getAnnouncementTinyDetailsById(Long id) {
        try {
            log.info("Service层查询公告详情 (用户版) , ID:{}", id);

            // 校验ID
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // 调用Manager层用户版方法
            return announcementManager.getAnnouncementTinyDetailsById(id);
        } catch (IllegalArgumentException e) {
            // 包装校验异常为参数错误
            log.warn("查询公告详情校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            log.error("查询公告详情数据库操作异常", e);
            throw new ApiException(ExceptionEnum.DATABASE_ERROR);
        } catch (Exception e) {
            // 重新抛出其他异常
            log.error("查询公告详情未知异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    // 用户查询公告列表
    @Override
    public ListAnnouncementResponse listAnnouncements(ListAnnouncementRequest request) {
        try {
            log.info("Service层查询公告列表, 页码:{}, 状态:{}", request.getPage(), request.getStatus());

            // 参数校验
            if (request.getPage() != null && request.getPage() < 1) {
                throw new IllegalArgumentException("页码必须大于0");
            }
            if (request.getSize() != null && (request.getSize() < 1 || request.getSize() > 50)) {
                throw new IllegalArgumentException("每页大小必须在1-50之间");
            }

            // 设置默认值
            if (request.getPage() == null)
                request.setPage(1);
            if (request.getSize() == null)
                request.setSize(8);
            if (request.getStatus() == null)
                request.setStatus(1); // 默认查询已发布的公告

            return announcementManager.listAnnouncements(request);
        } catch (IllegalArgumentException e) {
            // 包装校验异常为参数错误
            log.warn("查询公告列表校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("查询公告列表异常-manager:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 重新抛出其他异常
            log.error("查询公告列表未知异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    // 管理员查询公告列表
    @Override
    public ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request) {
        try {
            log.info("Service层管理员查询公告列表, 页码:{}, 状态:{}, 排序方向:{}",
                    request.getPage(), request.getStatus(), request.orderType());

            // 参数校验
            if (request.getPage() != null && request.getPage() < 1) {
                throw new IllegalArgumentException("页码必须大于0");
            }
            if (request.getSize() != null && (request.getSize() < 1 || request.getSize() > 100)) {
                throw new IllegalArgumentException("每页大小必须在1-100之间");
            }
            if (request.getStatus() != null && (request.getStatus() < 0 || request.getStatus() > 2)) {
                throw new IllegalArgumentException("状态值必须在0-2之间 (0=草稿, 1=已发布, 2=待发布) ");
            }
            if (request.orderType() < 0 || request.orderType() > 1) {
                throw new IllegalArgumentException("排序方向必须为0 (升序) 或1 (降序) ");
            }

            // 设置默认值 (AdminQueryAnnouncementRequest已有默认值, 但防御性编程)
            if (request.getPage() == null)
                request.setPage(1);
            if (request.getSize() == null)
                request.setSize(8);
            if (request.getStatus() == null)
                request.setStatus(0);

            return announcementManager.adminQueryAnnouncements(request);

        } catch (IllegalArgumentException e) {
            // 包装业务校验异常为参数错误
            log.warn("管理员查询公告列表校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (ForumServiceException e) {
            // Manager的异常
            log.warn("查询公告列表异常-manager:{}", e.getMessage());
            throw new ApiException(e);
        } catch (Exception e) {
            // 重新抛出其他异常
            log.error("查询公告列表未知异常", e);
            throw new ApiException(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 校验标题和内容长度 (作为防御性编程, 虽然DTO层已有校验, 但Service层保留以确保数据安全)
     */
    private void validateTitleAndContent(String title, String content) {
        // 校验标题长度 (2-50字符)
        String trimmedTitle = title != null ? title.trim() : null;
        if (trimmedTitle == null || trimmedTitle.length() < 2 || trimmedTitle.length() > 50) {
            throw new IllegalArgumentException("公告标题长度必须在2-50字符之间");
        }

        // 校验内容长度 (2-500字符)
        String trimmedContent = content != null ? content.trim() : null;
        if (trimmedContent == null || trimmedContent.length() < 2 || trimmedContent.length() > 500) {
            throw new IllegalArgumentException("公告内容长度必须在2-500字符之间");
        }
    }

    /**
     * 校验公告类型 (作为防御性编程, 虽然DTO层已有校验, 但Service层保留以确保数据安全)
     */
    private void validateAnnouncementType(int type) {
        if (type != 0 && type != 1) {
            throw new IllegalArgumentException("公告类型无效, 仅支持系统公告(0)和学校公告(1)");
        }
    }

    /**
     * 校验创建时的定时发布和状态逻辑
     */
    private void validateScheduledAndStatus(ZonedDateTime scheduledAt, Integer status) {
        if (scheduledAt != null) {
            // 获取当前时区 (UTC+8) 时间
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
            // 设置最小允许时间 (当前时间+30秒)
            ZonedDateTime minAllowedTime = now.plusSeconds(30);

            // 比较带时区的时间
            if (scheduledAt.isBefore(minAllowedTime)) {
                throw new IllegalArgumentException("定时发布时间必须至少在当前时间30秒之后");
            }

            // scheduled_at非空时, status只能为2
            if (status == null || status != 2) {
                throw new IllegalArgumentException("已设置定时发布, 状态已锁定");
            }
        } else {
            // scheduled_at为空时, status可以为0或1
            if (status != null && status != 0 && status != 1) {
                throw new IllegalArgumentException("未设置定时发布时, 状态只能为草稿或已发布");
            }
        }
    }
}
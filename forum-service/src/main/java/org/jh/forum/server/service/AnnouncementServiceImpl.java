package org.jh.forum.server.service;

import java.time.LocalDateTime;

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
    private AnnouncementManager announcementManager; // 创建公告

    @Override
    public AnnouncementOperationResponse createAnnouncement(CreateAnnouncementRequest request) {
        try {

            // Service层处理校验
            validateTitleAndContent(request.getTitle(), request.getContent());

            // 标题查重校验（使用trim后的标题）
            String trimmedTitle = request.getTitle().trim();
            if (announcementManager.checkTitleDuplicate(trimmedTitle)) {
                throw new IllegalArgumentException("公告标题已存在，请使用其他标题");
            } // 校验公告类型
            validateAnnouncementType(request.getType());

            // 校验定时发布和状态逻辑
            validateScheduledAndStatus(request.getScheduledAt(), request.getStatus());

            // 如果设置为置顶，检查置顶公告数量限制（最多3个）
            if (request.getSticky() && !announcementManager.canStickyAnnouncement()) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            // Service层DTO->Entity转换
            Announcement entity = convertToEntity(request);

            // Manager层执行原子数据库操作
            Announcement saved = announcementManager.createAnnouncement(entity);

            AnnouncementOperationResponse response = new AnnouncementOperationResponse();
            response.setAnnounceId(saved.getId());
            return response;
        } catch (IllegalArgumentException e) {
            // 包装参数错误
            log.warn("创建公告校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e); // 记录完整的SQLException堆栈信息
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常
            log.error("未知异常", e); // 记录未处理的异常堆栈信息
            throw e;
        }
    }

    // DTO转Entity 这里缺少一点特殊字段驼峰-蛇形转换，现在先这么写着
    private Announcement convertToEntity(CreateAnnouncementRequest request) {
        // 直接使用LocalDateTime，无需时区转换
        LocalDateTime scheduledAt = request.getScheduledAt();
        if (scheduledAt != null) {
            log.debug("使用定时发布时间: {}", scheduledAt);
        } // TODO: 更好的autofill
        return Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .createUid(123L) // Mock创建人ID TODO: 替换为实际获取的用户ID
                .updateUid(123L) // 创建时设置更新人为创建人
                .scheduledAt(scheduledAt).status(request.getStatus() != null ? request.getStatus() : 0)
                .deleted(false) // 新创建的公告默认未删除
                .attribute(convertAttributeToString(request.getAttribute()))
                .sticky(request.getSticky())
                .build();
    } 
    
    
    // 编辑公告

    @Override
    public AnnouncementOperationResponse editAnnouncement(Long id, EditAnnouncementRequest request) {
        try {
            log.info("Service层编辑公告，ID：{}，标题：{}", id, request.getTitle());

            // 统一存在性校验
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // 校验标题和内容
            validateTitleAndContent(request.getTitle(), request.getContent());

            // 标题查重校验（编辑时排除当前公告ID）
            String trimmedTitle = request.getTitle().trim();
            if (announcementManager.checkTitleDuplicate(trimmedTitle, id)) {
                throw new IllegalArgumentException("公告标题已存在，请使用其他标题");
            }

            // 校验公告类型
            validateAnnouncementType(request.getType());

            // 校验定时发布和状态逻辑
            validateScheduledAndStatus(request.getScheduledAt(), request.getStatus());

            Announcement originAnnouncement = announcementManager.getAnnouncementEntityById(id);

            // 如果设置为置顶，检查置顶公告数量限制（最多3个）
            if (request.getSticky() != null && request.getSticky()) {
                // 只有在希望置顶且当前公告未置顶时，才检查数量限制
                if (!announcementManager.canStickyAnnouncement(id)) {
                    throw new IllegalArgumentException("置顶公告数量已达上限");
                }
            }

            // TODO 编辑权限校验（等着CurrentUid上线）
            // if (originAnnouncement.getCreateUid() != currentUid && currentRole != 2) {
            // throw new IllegalArgumentException("您没有编辑该公告的权限");
            // }

            // 内联权限状态检验
            if (originAnnouncement.getStatus() == 1) {
                // 如果当前公告为已发布状态，则不允许编辑定时发布和状态
                if (request.getScheduledAt() != null || request.getScheduledAt() != originAnnouncement.getScheduledAt()
                        || request.getStatus() != 1) {
                    throw new IllegalArgumentException("已发布的公告不允许编辑定时发布和状态");
                }
                if (request.getStatus() != null && request.getStatus() != 1) {
                    throw new IllegalArgumentException("已发布的公告不允许修改发布状态");
                }
                // 执行基础字段更新（只能编辑标题、内容、类型、属性、置顶）
                return announcementManager.editBasicFields(id, request);
            } else {
                // 草稿(0)和待发布(2)状态的公告可以编辑所有字段
                return announcementManager.editAllFields(id, request);
            }

        } catch (IllegalArgumentException e) {
            // 包装校验异常为参数错误
            log.warn("编辑公告校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e); // 添加详细日志
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常
            log.error("未知异常", e); // 添加详细日志
            throw e;
        }
    }

    // 置顶/取消置顶公告
    @Override
    public AnnouncementOperationResponse stickyAnnouncement(Long id, Boolean sticky) {
        try {
            log.info("Service层置顶/取消置顶公告，ID：{}，置顶状态：{}", id, sticky);

            // 校验sticky参数（防御性编程）
            if (sticky == null) {
                throw new IllegalArgumentException("置顶状态不能为空，必须为true或false");
            }

            // 校验ID并检查公告是否存在且未被删除
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // TODO 编辑权限校验（等着CurrentUid上线）
            // if (originAnnouncement.getCreateUid() != currentUid && currentRole != 2) {
            // throw new IllegalArgumentException("您没有编辑该公告的权限");
            // }

            // 如果置顶，检查置顶公告数量限制（最多3个）
            if (sticky && !announcementManager.canStickyAnnouncement(id)) {
                throw new IllegalArgumentException("置顶公告数量已达上限");
            }

            return announcementManager.stickyAnnouncement(id, sticky);

        } catch (IllegalArgumentException e) {
            // 直接使用异常消息，不添加前缀
            String errorMsg = e.getMessage() != null ? e.getMessage() : "参数错误";
            log.warn("置顶公告校验失败: {}", errorMsg);
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), errorMsg);
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e);
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常时也要包装
            log.error("置顶公告系统异常", e);
            throw e;
        }
    }

    // 删除公告
    @Override
    public AnnouncementOperationResponse deleteAnnouncement(Long id) {
        try {
            log.info("Service层删除公告，ID：{}", id);
            // 校验ID
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }
            // TODO 删除权限校验（等着CurrentUid上线)
            // if (originAnnouncement.getCreateUid() != currentUid && currentRole != 2) {
            // throw new IllegalArgumentException("您没有删除该公告的权限");
            // }

            // 执行删除操作
            return announcementManager.deleteAnnouncement(id);
        } catch (IllegalArgumentException e) {
            // 直接使用异常消息，不添加前缀
            String errorMsg = e.getMessage() != null ? e.getMessage() : "参数错误";
            log.warn("删除公告校验失败:");
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), errorMsg);
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e);
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常时也要包装
            log.error("删除公告系统异常", e);
            throw e;
        }
    }

    // 根据ID查询公告详情（管理员）
    @Override
    public AnnouncementDetailsResponse getAnnouncementById(Long id) {
        try {
            log.info("Service层查询公告详情，ID：{}", id);

            // 校验ID
            if (!announcementManager.checkExist(id)) {
                throw new IllegalArgumentException("公告不存在或已被删除");
            }

            // TODO 查询权限校验（仅管理可以查看所有参数）

            // 具体查询操作
            return announcementManager.getAnnouncementById(id);
        } catch (IllegalArgumentException e) {
            // 包装校验异常为参数错误
            log.warn("查询公告详情校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e);
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常
            log.error("查询公告详情未知异常", e);
            throw new ApiException(500, 50000, "查询公告详情失败：系统内部错误");
        }
    }

    // 根据ID查询公告详情（用户版本）
    @Override
    public AnnouncementTinyDetailsResponse getAnnouncementTinyDetailsById(Long id) {
        try {
            log.info("Service层查询公告详情（用户版），ID：{}", id);

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
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e);
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常
            log.error("查询公告详情未知异常", e);
            throw new ApiException(500, 50000, "查询公告详情失败：系统内部错误");
        }
    }

    // 用户查询公告列表
    @Override
    public ListAnnouncementResponse listAnnouncements(ListAnnouncementRequest request) {
        try {
            log.info("Service层查询公告列表，页码：{}，状态：{}", request.getPage(), request.getStatus());

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
            // 包装业务校验异常为参数错误
            log.warn("查询公告列表校验失败: {}", e.getMessage());
            throw new ApiException(200, ExceptionEnum.INVALID_PARAMETER.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            // 包装数据库异常
            if (e instanceof java.sql.SQLException ||
                    (e.getCause() != null && e.getCause() instanceof java.sql.SQLException)) {
                log.error("数据库操作异常", e);
                throw new ApiException(ExceptionEnum.DATABASE_ERROR);
            }
            // 重新抛出其他异常
            log.error("查询公告列表未知异常", e);
            throw new ApiException(500, 50000, "查询公告列表失败：系统内部错误");
        }
    }

    // 管理员查询公告列表
    @Override
    public ListAnnouncementResponse adminQueryAnnouncements(AdminQueryAnnouncementRequest request) {
        // log.info("Service层管理员查询公告列表，页码：{}，筛选条件：{}", request.getPage(),
        // request.getFilters());
        // TODO: 这里先返回mock数据，后续实现真正的管理员查询逻辑
        return announcementManager.adminQueryAnnouncements(request);
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
     * 校验标题和内容长度（作为防御性编程，虽然DTO层已有校验，但Service层保留以确保数据安全）
     */
    private void validateTitleAndContent(String title, String content) {
        // 校验标题长度（2-50字符）
        String trimmedTitle = title != null ? title.trim() : null;
        if (trimmedTitle == null || trimmedTitle.length() < 2 || trimmedTitle.length() > 50) {
            throw new IllegalArgumentException("公告标题长度必须在2-50字符之间");
        }

        // 校验内容长度（2-500字符）
        String trimmedContent = content != null ? content.trim() : null;
        if (trimmedContent == null || trimmedContent.length() < 2 || trimmedContent.length() > 500) {
            throw new IllegalArgumentException("公告内容长度必须在2-500字符之间");
        }
    }

    /**
     * 校验公告类型（作为防御性编程，虽然DTO层已有校验，但Service层保留以确保数据安全）
     */
    private void validateAnnouncementType(int type) {
        if (type != 0 && type != 1) {
            throw new IllegalArgumentException("公告类型无效，仅支持系统公告(0)和学校公告(1)");
        }
    }

    /**
     * 校验创建时的定时发布和状态逻辑
     */
    private void validateScheduledAndStatus(LocalDateTime scheduledAt, Integer status) {
        if (scheduledAt != null) {
            // scheduled_at非空时，必须是未来时间（+30秒保底）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime minAllowedTime = now.plusSeconds(30);

            if (scheduledAt.isBefore(minAllowedTime)) {
                throw new IllegalArgumentException("定时发布时间必须至少在当前时间30秒之后");
            }

            // scheduled_at非空时，status只能为2
            if (status == null || status != 2) {
                throw new IllegalArgumentException("已设置定时发布，状态已锁定");
            }
        } else {
            // scheduled_at为空时，status可以为0或1
            if (status != null && status != 0 && status != 1) {
                throw new IllegalArgumentException("未设置定时发布时，状态只能为草稿或已发布");
            }
        }
    }

    /**
     * 校验逻辑
     * TODO: 实现权限校验逻辑
     */
    @SuppressWarnings("unused")
    private void checkPermission(Integer id, Long currentUid) {
        // TODO: 检验当前用户id和公告创建者id是否一致
    }
}
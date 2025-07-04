package org.jh.forum.start.controller;

import org.jh.forum.api.service.AnnouncementService;
import org.jh.forum.server.manager.AnnouncementManager;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.StickyAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailResponse;
import org.jh.forum.common.dto.response.AnnouncementTinyDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementTinyResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.common.entity.Announcement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告管理控制器
 * ·*
 * 功能概述:
 * - 创建公告: POST /announcements
 * - 更新公告: PUT /announcements
 * - 设置/取消置顶: PUT /announcements/sticky
 * - 软删除公告: DELETE /announcements?id=123
 * - 用户查看公告详情: GET /announcements?id=123
 * - 管理员查看公告全部信息: GET /announcement/see?id=123
 * - 用户公告列表: GET /announcements/list?conditions
 * - 管理员公告列表: GET /announcements/query?conditions
 * 开发链路说明:
 * 1. Controller 接收HTTP请求
 * 2. 调用 Service(RPC) 处理请求
 * 3. Service 调用 Manager 处理逻辑
 * 4. Manager 调用 Mapper 操作数据库
 * 5. 返回结果给前端
 *
 * @author SituChengxiang( SK )
 */
@Slf4j
@RestController
@RequestMapping("/announcements")
@Tag(name = "公告管理接口", description = "公告的创建、发布、查询等功能")
public class AnnouncementController {
    private  final  String superAdminRole = "super_admin";

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private AnnouncementManager announcementManager;

    /**
     * 创建公告接口
     * HTTP方法: POST
     * 请求路径: /announcements
     * 请求体: JSON格式 { title, content, type, scheduledAt, status, attribute, sticky }
     * 权限: 管理员
     *
     * @param request 创建公告的请求参数
     * @return 创建结果, 包含公告基本信息
     */
    @Operation(summary = "创建公告", description = "创建一个公告, 支持多种类型( 管理员权限 )")
    @SaCheckLogin
    @SaCheckRole(value = { "admin", "super_admin" }, mode = SaMode.OR)
    @PostMapping
    public AjaxResult<AnnouncementOperationResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        Long currentUid = StpUtil.getLoginIdAsLong();
        try {
            log.info("收到创建公告请求, 标题: {}, 类型: {}, 预计时间: {}, 操作人ID: {}",
                    request.getTitle(), request.getType(), request.getScheduledAt(), currentUid);

            AnnouncementOperationResponse response = announcementService.createAnnouncement(request);

            log.info("公告创建成功, ID: {}", response.getAnnounceId());
            return AjaxResult.success("created", response);
        } catch (ApiException e) {
            log.warn("创建公告失败: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("创建公共失败, 未知错误: {}", e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 更新公告接口
     * HTTP方法: PUT
     * 请求路径: /announcements
     * 请求体: JSON格式 { id, title, content, type, scheduledAt, status }
     * 权限: 管理员
     *
     * @param request 编辑公告的请求参数
     * @return 编辑结果
     */
    @Operation(summary = "更新公告", description = "更新指定ID的公告信息( 管理员权限 )")
    @SaCheckLogin
    @SaCheckRole(value = { "admin", "super_admin" }, mode = SaMode.OR)
    @PutMapping
    public AjaxResult<AnnouncementOperationResponse> editAnnouncement(
            @Valid @RequestBody EditAnnouncementRequest request) {

        Long currentUid = StpUtil.getLoginIdAsLong();

        // 若为超级管理员, 允许操作
        if (!StpUtil.hasRole(superAdminRole)) {
            // 否则检查是否为公告创建者
            boolean isCreator = announcementManager.isAnnouncementCreator(request.getId(), currentUid);
            if (!isCreator) {
                log.warn("用户无权限修改该公告, ID: {}, 用户ID: {}", request.getId(), currentUid);
                return AjaxResult.fail(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }
        }

        log.info("收到更新公告请求, ID: {}, 标题: {}, 类型: {}, 操作人id: {}",
                request.getId(), request.getTitle(), request.getType(), currentUid);
        try {
            AnnouncementOperationResponse response = announcementService.editAnnouncement(request.getId(), request);

            log.info("公告更新成功, ID: {}", response.getAnnounceId());
            return AjaxResult.success("updated", response);
        } catch (ApiException e) {
            log.warn("更新公告失败: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("更新公告失败, 未知错误, ID: {}, 异常: {}", request.getId(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 设置/取消置顶公告接口
     * HTTP方法: PUT
     * 请求路径: /announcements/sticky
     * 请求体: JSON格式 { "id":12, "sticky": true }
     * 权限: 管理员
     *
     * @param request 置顶状态请求参数
     * @return 操作结果
     */
    @Operation(summary = "设置/取消置顶公告", description = "设置或取消公告的置顶状态( 管理员权限 )")
    @SaCheckLogin
    @SaCheckRole(value = { "admin", "super_admin" }, mode = SaMode.OR)
    @PutMapping("/sticky")
    public AjaxResult<AnnouncementOperationResponse> stickyAnnouncement(
            @Valid @RequestBody StickyAnnouncementRequest request) {
        Long currentUid = StpUtil.getLoginIdAsLong();

        // 若为超级管理员, 允许操作
        if (!StpUtil.hasRole(superAdminRole)) {
            // 否则检查是否为公告创建者
            boolean isCreator = announcementManager.isAnnouncementCreator(request.getId(), currentUid);
            if (!isCreator) {
                log.warn("用户无权限修改该公告置顶状态, ID: {}, 用户ID: {}", request.getId(), currentUid);
                return AjaxResult.fail(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }
        }
        log.info("收到置顶/取消置顶公告请求, ID: {}, 置顶状态: {}", request.getId(), request.getSticky());
        try {

            AnnouncementOperationResponse response = announcementService.stickyAnnouncement(
                    request.getId(), request.getSticky());

            log.info("公告{}成功, ID: {}", request.getSticky() ? "置顶" : "取消置顶", response.getAnnounceId());
            return AjaxResult.success(request.getSticky() ? "stickied" : "unstick", response);
        } catch (ApiException e) {
            // 处理 ApiException( Service层包装的异常 )
            log.error("置顶/取消公告异常 {}", e.getMessage(),e);
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("置顶/取消置顶公告未知异常, ID: {}, 未知错误: {}", request.getId(), e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 软删除公告接口
     * HTTP方法: DELETE
     * 请求路径: /announcements?id=123
     * 请求体: 无( 直接标记 deleted = true )
     * 权限: 管理员
     *
     * @param id 公告ID
     * @return 删除结果
     */
    @Operation(summary = "软删除公告", description = "软删除指定公告( 管理员权限 )")
    @SaCheckLogin
    @SaCheckRole(value = { "admin", "super_admin" }, mode = SaMode.OR)
    @DeleteMapping
    public AjaxResult<AnnouncementOperationResponse> deleteAnnouncement(@RequestParam("id") Long id) {
        Long currentUid = StpUtil.getLoginIdAsLong();

        // 若为超级管理员, 允许操作
        if (!StpUtil.hasRole(superAdminRole)) {
            // 否则检查是否为公告创建者
            boolean isCreator = announcementManager.isAnnouncementCreator(id, currentUid);
            if (!isCreator) {
                log.warn("用户无权限删除该公告, ID: {}, 用户ID: {}", id, currentUid);
                return AjaxResult.fail(ExceptionEnum.PERMISSION_NOT_ALLOWED);
            }
        }

        try {
            log.info("收到删除公告请求, ID: {}", id);
            AnnouncementOperationResponse result = announcementService.deleteAnnouncement(id);

            log.info("删除公告成功, ID: {}", id);
            return AjaxResult.success("deleted", result);
        } catch (ApiException e) {
            // 处理 ApiException( Service层包装的异常 )
            log.error("删除公告异常: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("删除公告失败, ID: {}, 未知错误: {}", id, e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 查看公告基本内容接口
     * HTTP方法: GET
     * 请求路径: /announcements?id=123
     * 权限: 用户
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @Operation(summary = "查看公告基本信息", description = "根据ID查询公告详情(用户)")
    @GetMapping
    public AjaxResult<AnnouncementTinyDetailsResponse> getAnnouncementTinyDetail(@RequestParam("id") Long id) {
        try {
            log.info("收到查询公告详情请求, ID: {}", id);
            AnnouncementTinyDetailsResponse response = announcementService.getAnnouncementTinyDetailsById(id);

            return AjaxResult.success(response);
        } catch (ApiException e) {
            // 处理 ApiException( Service层包装的异常 )
            log.error("获取公告异常: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("获取公告失败, ID: {}, 未知错误: {}", id, e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 查看公告全部信息接口
     * HTTP方法: GET
     * 请求路径: /announcements/see?id=123
     * 权限: 管理员
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @Operation(summary = "查看公告全部信息", description = "根据ID查询公告详情( 管理员 )")
    @SaCheckLogin
    @SaCheckRole(value = { "admin", "super_admin" }, mode = SaMode.OR)
    @GetMapping("/see")
    public AjaxResult<AnnouncementDetailResponse> getAnnouncementDetail(@RequestParam("id") Long id) {
        try {
            log.info("收到admin查询公告详情请求, ID: {}", id);
            AnnouncementDetailResponse response = announcementService.getAnnouncementById(id);

            return AjaxResult.success(response);
        } catch (ApiException e) {
            // 处理 ApiException( Service层包装的异常 )
            log.error("controller-admin获取公告异常: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("获取公告失败, ID: {}, 未知错误: {}", id, e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 用户公告列表接口
     * HTTP方法: GET
     * 请求路径: /announcements/list
     * 支持参数: page, size, type
     * 默认排序: 按 updated_at 升序( 最后发布的在最下面 )
     * 权限: 用户
     * 
     * @param request 用户查询请求参数
     * @return 公告列表( 简化版 )
     */
    @Operation(summary = "用户公告列表", description = "查询用户可见的公告列表, 按 updated_at 升序( 最后发布的在最下面 )")
    @GetMapping("/list")
    public AjaxResult<ListAnnouncementTinyResponse> listAnnouncements(@Valid UserQueryAnnouncementRequest request) {
        try {
            log.info("收到查询公告列表请求, 页码: {}, 大小: {}, 类型: {}", request.getPage(), request.getSize(), request.getType());
            // Type 请求-db转换
            switch (request.getType()) {
                case 1:
                    request.setType(Announcement.AnnouncementType.SYSTEM.getCode());
                    break;
                case 2:
                    request.setType(Announcement.AnnouncementType.SCHOOLING.getCode());
                    break;
                default:
                    request.setType(null); 
                    // 不限制类型
                    break;
            }

            // 只查询已发布的公告并转换为 ListAnnouncementTinyResponse
            ListAnnouncementTinyResponse response = announcementService.userListAnnouncements(request);

            log.info("查询公告列表成功, 总数: {}", response.getTotal());
            return AjaxResult.success(response);
        } catch (ApiException e) {
            // 处理 ApiException( Service层包装的异常 )
            log.error("查询公告列表异常: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("查询公告列表失败, 未知错误: {}", e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * 管理员公告列表查询接口
     * HTTP方法: GET
     * 请求路径: /announcements/query
     * 请求参数: 使用 AdminQueryAnnouncementRequest DTO 封装所有参数
     * 权限: 管理员
     *
     * @param request 管理员查询请求参数
     * @return 公告列表
     */
    @Operation(summary = "管理员公告列表查询", description = "查询管理员可见的公告列表( 管理员权限 )")
    @SaCheckLogin
    @SaCheckRole(value = { "admin", "super_admin" }, mode = SaMode.OR)
    @GetMapping("/query")
    public AjaxResult<ListAnnouncementResponse> adminAnnouncementQueryRequest(
            @Valid AdminQueryAnnouncementRequest request) {
        try {
            log.info("收到管理员查询公告列表请求, 页码: {}, 状态: {}, 排序方向: {}, 是否查询已删除: {}",
                    request.getPage(), request.getStatus(), request.orderType(), request.getDeleted());
            
            // Type 请求-db转换
            switch (request.getType()) {
                case 1:
                    request.setType(Announcement.AnnouncementType.SYSTEM.getCode());
                    break;
                case 2:
                    request.setType(Announcement.AnnouncementType.SCHOOLING.getCode());
                    break;
                default:
                    request.setType(null); 
                    // 不限制类型
                    break;
            }

            ListAnnouncementResponse response = announcementService.adminQueryAnnouncements(request);

            log.info("管理员查询公告列表成功, 总数: {}", response.getTotal());
            return AjaxResult.success(response);
        } catch (ApiException e) {
            // 处理 ApiException( Service层包装的异常 )
            log.error("管理员查询公告列表异常: {}", e.getErrorMsg());
            return AjaxResult.fail(e.getErrorCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("管理员查询公告列表失败, 未知错误: {}", e.getMessage(), e);
            return AjaxResult.fail(ExceptionEnum.UNKNOWN_ERROR);
        }
    }
    
    /**
     * 手动触发定时发布任务接口( 测试用 )
     * HTTP方法: POST
     * 请求路径: /announcements/trigger-publish
     * 权限: 管理员
     *
     * @return 触发结果
     */
//     @Deprecated
//     @Operation(summary = "手动触发定时发布", description = "手动触发定时发布任务( 测试用, 管理员权限 )")
//     // @SaCheckLogin
//     // @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
//     @PostMapping("/trigger-publish")
//     public AjaxResult<String> triggerScheduledPublish() {
//     try {
//     log.info("收到手动触发定时发布请求");
//     scheduleService.manualTriggerPublish();
//     return AjaxResult.success("定时发布任务已触发");
//     } catch (Exception e) {
//     log.error("手动触发定时发布失败", e);
//     return AjaxResult.fail(500, "触发定时发布失败: " + e.getMessage());
//     }
//     }
}



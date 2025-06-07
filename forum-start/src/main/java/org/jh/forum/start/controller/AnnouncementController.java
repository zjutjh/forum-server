package org.jh.forum.start.controller;

import org.jh.forum.api.service.AnnouncementService;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.dto.request.AdminQueryAnnouncementRequest;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.request.StickyAnnouncementRequest;
import org.jh.forum.common.dto.request.UserQueryAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnoucementTinyResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * 公告管理控制器
 * ·*
 * 功能概述：
 * - 创建公告：POST /announcements
 * - 更新公告：PUT /announcements
 * - 设置/取消置顶：PUT /announcements/sticky
 * - 软删除公告：DELETE /announcements?id=123
 * - 查看公告详情：GET /announcements?id=123
 * - 用户公告列表：GET /announcements/list?conditions
 * - 管理员公告列表：GET /announcements/query?conditions
 * 
 * 开发链路说明：
 * 1. Controller 接收HTTP请求
 * 2. 调用 Service(RPC) 处理请求
 * 3. Service 调用 Manager 处理逻辑
 * 4. Manager 调用 Mapper 操作数据库
 * 5. 返回结果给前端
 *
 * @author SituChengxiang（SK）
 */
@Slf4j
@RestController
@RequestMapping("/announcements")
@Tag(name = "公告管理接口", description = "公告的创建、发布、查询等功能")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    /**
     * 创建公告接口
     *
     * HTTP方法：POST
     * 请求路径：/announcements
     * 请求体：JSON格式 { title, content, type, scheduledAt, status, attribute, sticky }
     * 权限：管理员
     *
     * @param request 创建公告的请求参数
     * @return 创建结果，包含公告基本信息
     */
    @Operation(summary = "创建公告", description = "创建一个公告，支持多种类型（管理员权限）")
    @PostMapping
    public AjaxResult<AnnouncementOperationResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        try {
            log.info("收到创建公告请求，标题: {}, 类型: {}, 预计时间：{}",
                    request.getTitle(), request.getType(), request.getScheduledAt());

            AnnouncementOperationResponse response = announcementService.createAnnouncement(request);

            log.info("公告创建成功，ID: {}", response.getAnnounceId());
            return AjaxResult.success("created", response);
        } catch (Exception e) {
            // 优先处理 ApiException（Service层包装的异常）
            if (e instanceof org.jh.forum.common.exceptions.ApiException) {
                org.jh.forum.common.exceptions.ApiException apiEx = (org.jh.forum.common.exceptions.ApiException) e;
                log.error("创建公告异常", apiEx); // 记录完整的ApiException堆栈
                return AjaxResult.fail(apiEx.getErrorCode(), apiEx.getErrorMsg());
            }
            // 兼容原有的数据库异常处理
            if (e.getCause() instanceof java.sql.SQLSyntaxErrorException) {
                log.error("数据库异常", e); // 记录完整的SQLSyntaxErrorException堆栈
                return AjaxResult.fail(ExceptionEnum.DATABASE_ERROR);
            }
            log.error("创建公告失败，标题: {}", request.getTitle(), e); // 记录完整的异常堆栈
            return AjaxResult.fail(500, "创建公告失败：" + e.getMessage());
        }
    }

    /**
     * 更新公告接口
     *
     * HTTP方法：PUT
     * 请求路径：/announcements
     * 请求体：JSON格式 { id, title, content, type, scheduledAt, status }
     * 权限：管理员
     *
     * @param request 编辑公告的请求参数
     * @return 编辑结果
     */
    @Operation(summary = "更新公告", description = "更新指定ID的公告信息（管理员权限）")
    @PutMapping
    public AjaxResult<AnnouncementOperationResponse> editAnnouncement(
            @Valid @RequestBody EditAnnouncementRequest request) {
        try {
            log.info("收到更新公告请求，ID: {}, 标题: {}, 类型: {}",
                    request.getId(), request.getTitle(), request.getType());

            AnnouncementOperationResponse response = announcementService.editAnnouncement(request.getId(), request);

            if (response == null) {
                log.warn("更新公告失败，公告可能不存在，ID: {}", request.getId());
                return AjaxResult.fail(404, "公告不存在或更新失败");
            }

            log.info("公告更新成功，ID: {}", response.getAnnounceId());
            return AjaxResult.success("updated", response);
        } catch (Exception e) {
            // 优先处理 ApiException（Service层包装的异常）
            if (e instanceof org.jh.forum.common.exceptions.ApiException) {
                org.jh.forum.common.exceptions.ApiException apiEx = (org.jh.forum.common.exceptions.ApiException) e;
                log.error("更新公告异常", apiEx); // 记录完整的ApiException堆栈
                return AjaxResult.fail(apiEx.getErrorCode(), apiEx.getErrorMsg());
            }
            log.error("更新公告失败，ID: {}, 标题: {}", request.getId(), request.getTitle(), e); // 记录完整的异常堆栈
            return AjaxResult.fail(500, "更新公告失败：" + e.getMessage());
        }
    }

    /**
     * 设置/取消置顶公告接口
     *
     * HTTP方法：PUT
     * 请求路径：/announcements/sticky
     * 请求体：JSON格式 { "id":12, "sticky": true }
     * 权限：管理员
     *
     * @param request 置顶状态请求参数
     * @return 操作结果
     */
    @Operation(summary = "设置/取消置顶公告", description = "设置或取消公告的置顶状态（管理员权限）")
    @PutMapping("/sticky")
    public AjaxResult<AnnouncementOperationResponse> stickyAnnouncement(
            @Valid @RequestBody StickyAnnouncementRequest request) {
        try {
            log.info("收到置顶/取消置顶公告请求，ID: {}, 置顶状态: {}", request.getId(), request.getSticky());

            AnnouncementOperationResponse response = announcementService.stickyAnnouncement(
                    request.getId(), request.getSticky());

            if (response == null) {
                log.warn("置顶/取消置顶公告失败，公告可能不存在，ID: {}", request.getId());
                return AjaxResult.fail(404, "公告不存在或操作失败");
            }

            log.info("公告{}成功，ID: {}", request.getSticky() ? "置顶" : "取消置顶", response.getAnnounceId());
            return AjaxResult.success(request.getSticky() ? "stickied" : "unstickied", response);
        } catch (Exception e) {
            // 优先处理 ApiException（Service层包装的异常）
            if (e instanceof org.jh.forum.common.exceptions.ApiException) {
                org.jh.forum.common.exceptions.ApiException apiEx = (org.jh.forum.common.exceptions.ApiException) e;
                log.error("置顶公告异常", apiEx);
                return AjaxResult.fail(apiEx.getErrorCode(), apiEx.getErrorMsg());
            }
            log.error("置顶/取消置顶公告失败，ID: {}, 错误信息: {}", request.getId(), e.getMessage(), e);
            return AjaxResult.fail(500, "置顶/取消置顶公告失败：" + e.getMessage());
        }
    }

    /**
     * 软删除公告接口
     *
     * HTTP方法：DELETE
     * 请求路径：/announcements?id=123
     * 请求体：无（直接标记 deleted = true）
     * 权限：管理员
     *
     * @param id 公告ID
     * @return 删除结果
     */
    @Operation(summary = "软删除公告", description = "软删除指定公告（管理员权限）")
    @DeleteMapping
    public AjaxResult<AnnouncementOperationResponse> deleteAnnouncement(@RequestParam("id") Integer id) {
        try {
            log.info("收到删除公告请求，ID: {}", id);
            AnnouncementOperationResponse result = announcementService.deleteAnnouncement(id);

            if (result == null) {
                log.warn("删除公告失败，公告可能不存在，ID: {}", id);
                return AjaxResult.fail(404, "公告不存在或已被删除");
            }

            log.info("删除公告成功，ID: {}", id);
            return AjaxResult.success("deleted", result);
        } catch (Exception e) {
            // 优先处理 ApiException（Service层包装的异常）
            if (e instanceof org.jh.forum.common.exceptions.ApiException) {
                org.jh.forum.common.exceptions.ApiException apiEx = (org.jh.forum.common.exceptions.ApiException) e;
                log.error("删除公告异常", apiEx);
                return AjaxResult.fail(apiEx.getErrorCode(), apiEx.getErrorMsg());
            }
            log.error("删除公告失败，ID: {}, 错误信息: {}", id, e.getMessage(), e);
            return AjaxResult.fail(500, "删除公告失败：" + e.getMessage());
        }
    }

    /**
     * 查看公告详情接口
     *
     * HTTP方法：GET
     * 请求路径：/announcements?id=123
     * 权限：用户/管理员
     *
     * @param id 公告ID
     * @return 公告详情
     */
    @Operation(summary = "查看公告详情", description = "根据ID查询公告详情（用户/管理员）")
    @GetMapping
    public AjaxResult<AnnouncementDetailsResponse> getAnnouncementDetail(@RequestParam Integer id) {
        try {
            log.info("收到查询公告详情请求，ID: {}", id);

            // TODO: 数据库层面验证ID是否存在且未删除，暂时mock放过
            if (id == null || id <= 0) {
                log.warn("公告ID无效: {}", id);
                return AjaxResult.fail(400, "公告ID无效");
            }

            AnnouncementDetailsResponse response = announcementService.getAnnouncementById(id);

            if (response == null) {
                log.warn("公告不存在，ID: {}", id);
                return AjaxResult.fail(404, "公告不存在");
            }

            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("查询公告详情失败，ID: {}, 错误信息: {}", id, e.getMessage(), e);
            return AjaxResult.fail(500, "查询公告失败：" + e.getMessage());
        }
    }

    /**
     * 用户公告列表接口
     *
     * HTTP方法：GET
     * 请求路径：/announcements/list * 支持参数：page, size, type
     * 默认排序：按 updated_at 升序（最后发布的在最下面）
     * 权限：用户
     * 
     * @param request 用户查询请求参数
     * @return 公告列表（简化版）
     */
    @Operation(summary = "用户公告列表", description = "查询用户可见的公告列表，按 updated_at 升序（最后发布的在最下面）", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "简化版公告列表，只包含必要字段", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ListAnnoucementTinyResponse.class)))
    })
    @GetMapping("/list")
    public AjaxResult<ListAnnoucementTinyResponse> listAnnouncements(@Valid UserQueryAnnouncementRequest request) {
        try {
            log.info("收到查询公告列表请求，页码: {}, 大小: {}, 类型: {}", request.getPage(), request.getSize(), request.getType());
            // 创建请求对象
            ListAnnouncementRequest serviceRequest = new ListAnnouncementRequest();
            serviceRequest.setPage(request.getPage());
            serviceRequest.setSize(request.getSize());

            // 设置类型筛选
            if (request.getType() != null) {
                // 根据枚举值设置类型
                if (request.getType().getValue() == 1) {
                    // 1=系统公告
                    serviceRequest.setType(1);
                } else if (request.getType().getValue() == 2) {
                    // 2=学校公告
                    serviceRequest.setType(2);
                }
                // 如果是3(全部)，则不设置type筛选条件
            }

            // 只查询已发布的公告
            serviceRequest.setStatus(1); // 调用服务获取列表
            ListAnnouncementResponse serviceResponse = announcementService.listAnnouncements(serviceRequest);

            // 将 ListAnnouncementResponse 转换为 ListAnnoucementTinyResponse
            ListAnnoucementTinyResponse response = convertToTinyResponse(serviceResponse);

            log.info("查询公告列表成功，总数: {}", response.getTotal());
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("查询公告列表失败，错误信息: {}", e.getMessage(), e);
            return AjaxResult.fail(500, "查询公告列表失败：" + e.getMessage());
        }
    }

    /**
     * 将 ListAnnouncementResponse 转换为 ListAnnoucementTinyResponse
     * 
     * @param source 原始响应对象
     * @return 简化的响应对象
     */
    private ListAnnoucementTinyResponse convertToTinyResponse(ListAnnouncementResponse source) {
        if (source == null) {
            return null;
        }

        ListAnnoucementTinyResponse target = new ListAnnoucementTinyResponse(); // 设置分页信息
        target.setTotal(source.getTotal());
        target.setPage(source.getPage());
        target.setPageSize(source.getPageSize());

        // 转换列表项
        if (source.getList() != null && !source.getList().isEmpty()) {
            target.setList(source.getList().stream()
                    .map(this::convertToTinyItem)
                    .toList());
        }

        return target;
    }

    /**
     * 将公告列表项转换为简化版本
     * 
     * @param source 原始列表项
     * @return 简化的列表项
     */
    private ListAnnoucementTinyResponse.AnnouncementItemResponse convertToTinyItem(
            ListAnnouncementResponse.AnnouncementItemResponse source) {

        ListAnnoucementTinyResponse.AnnouncementItemResponse target = new ListAnnoucementTinyResponse.AnnouncementItemResponse();

        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setType(source.getType());
        target.setCreator(source.getCreator());
        target.setUpdator(source.getUpdator());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setSticky(source.isSticky());

        return target;
    }

    /**
     * 管理员公告列表查询接口
     *
     * HTTP方法：GET
     * 请求路径：/announcements/query
     * 请求参数：使用 AdminQueryAnnouncementRequest DTO 封装所有参数
     * 权限：管理员
     *
     * @param request 管理员查询请求参数
     * @return 公告列表
     */
    @Operation(summary = "管理员公告列表查询", description = "查询管理员可见的公告列表（管理员权限）")
    @GetMapping("/query")
    public AjaxResult<ListAnnouncementResponse> adminAnnouncementQueryRequest(
            @Valid AdminQueryAnnouncementRequest request) {
        try {
            log.info("收到管理员查询公告列表请求，页码: {}, 状态: {}, 排序字段: {}, 排序方向: {}, 是否查询已删除: {}",
                    request.getPage(), request.getStatus(), request.orderField(),
                    request.orderType(), request.getDeleted());

            ListAnnouncementResponse response = announcementService.adminQueryAnnouncements(request);

            log.info("管理员查询公告列表成功，总数: {}", response.getTotal());
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("管理员查询公告列表失败，错误信息: {}", e.getMessage(), e);
            return AjaxResult.fail(500, "查询公告列表失败：" + e.getMessage());
        }
    }
}

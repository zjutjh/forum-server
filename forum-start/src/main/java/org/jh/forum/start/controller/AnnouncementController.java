package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.AnnouncementService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 公告管理控制器
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
 * @author SituChengxiang(SK)
 */
@Slf4j
@RestController
@SaCheckLogin
@RequestMapping("/announcements")
@Tag(name = "公告", description = "公告的创建、发布、查询等功能")
public class AnnouncementController {

    @DubboReference
    private AnnouncementService announcementService;

    /**
     * 创建公告接口
     * HTTP方法: POST
     * 请求路径: /announcements
     * 请求体: JSON格式 { title, content, type, scheduledAt, status, attribute, sticky }
     * 权限: 管理员
     */
    @Operation(summary = "创建公告")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @PostMapping
    public AjaxResult<Void> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        announcementService.createAnnouncement(request);
        return AjaxResult.success();
    }

    /**
     * 编辑公告接口
     * HTTP方法: PUT
     * 请求路径: /announcements
     * 请求体: JSON格式 { id, title, content, type, scheduledAt, status }
     * 权限: 管理员
     */
    @Operation(summary = "编辑公告", description = "注意：若公告已发布，则对status和publishedAt的修改不会生效！！！")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @PutMapping
    public AjaxResult<Void> editAnnouncement(
            @Valid @RequestBody EditAnnouncementRequest request) {
        announcementService.editAnnouncement(request);
        return AjaxResult.success();
    }

    /**
     * 设置/取消置顶公告接口
     * HTTP方法: PUT
     * 请求路径: /announcements/sticky
     * 请求体: JSON格式 { "id":12, "sticky": true }
     * 权限: 管理员
     */
    @Operation(summary = "设置/取消置顶公告")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @PutMapping("/sticky")
    public AjaxResult<Void> stickyAnnouncement(
            @Valid @RequestBody StickyAnnouncementRequest request) {
        announcementService.stickyAnnouncement(request);
        return AjaxResult.success();
    }

    /**
     * 删除公告接口
     * HTTP方法: DELETE
     * 请求路径: /announcements?id=123
     * 权限: 管理员
     */
    @Operation(summary = "删除公告")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @DeleteMapping
    public AjaxResult<Void> deleteAnnouncement(@RequestParam("id") Long id) {
        announcementService.deleteAnnouncement(id);
        return AjaxResult.success();
    }

    /**
     * 查看公告全部信息接口
     * HTTP方法: GET
     * 请求路径: /announcements/see?id=123
     * 权限: 管理员
     */
    @Operation(summary = "管理员查看公告信息")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/see")
    public AjaxResult<GetAdminAnnouncementDetailResponse> getAdminAnnouncementDetail(@RequestParam("id") Long id) {
        return AjaxResult.success(announcementService.getAdminAnnouncementDetail(id));
    }

    /**
     * 用户公告列表接口
     * HTTP方法: GET
     * 请求路径: /announcements/list
     * 权限: 用户
     */
    @Operation(summary = "用户公告列表")
    @GetMapping("/list")
    public AjaxResult<BaseListResponse<GetAnnouncementListElement>> getAnnouncementList(
            @Valid GetAnnouncementListRequest request) {
        return AjaxResult.success(announcementService.userListAnnouncements(request));
    }

    /**
     * 管理员公告列表查询接口
     * HTTP方法: GET
     * 请求路径: /announcements/query
     * 权限: 管理员
     */
    @Operation(summary = "管理员公告列表查询")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/query")
    public AjaxResult<BaseListResponse<GetAdminAnnouncementListElement>> getAdminAnnouncementList(
            @Valid GetAdminAnnouncementListRequest request) {
        return AjaxResult.success(announcementService.adminQueryAnnouncements(request));
    }

    /**
     * 获取置顶的三条公告，不够的话最新更新的来凑
     * HTTP方法: GET
     * 请求路径: /announcements/top
     * 权限: 用户
     */
    @Operation(summary = "获取置顶公告", description = "获取置顶的三条公告，不够的话最新更新的来凑")
    @GetMapping("/top")
    public AjaxResult<StickyAnnouncementList> getTopAnnouncements() {
        return AjaxResult.success(announcementService.getTopAnnouncements());
    }
}

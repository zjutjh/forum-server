package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.ReportService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * @author zzb
 */
@Slf4j
@RestController
@RequestMapping("/report")
@SaCheckLogin
@Tag(name = "举报", description = "举报相关接口")
public class ReportController {
    @DubboReference
    private ReportService reportService;

    @Operation(summary = "举报用户")
    @PostMapping("/user")
    public AjaxResult<Void> reportUser(@Valid @RequestBody ReportUserRequest request) {
        reportService.reportUser(request);
        return AjaxResult.success();
    }

    @Operation(summary = "举报帖子/评论")
    @PostMapping("/content")
    public AjaxResult<Void> reportContent(@Valid @RequestBody ReportContentRequest request) {
        reportService.reportContent(request);
        return AjaxResult.success();
    }

    @Operation(summary = "处理举报")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @PostMapping("/handle")
    public AjaxResult<HandleReportResponse> handleReport(@Valid @RequestBody HandleReportRequest request) {
        return AjaxResult.success(reportService.handleReport(request));
    }

    @Operation(summary = "获取举报列表")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/list")
    public AjaxResult<BaseListResponse<GetReportListElement>> getReportList(@Valid GetReportListRequest request) {
        return AjaxResult.success(reportService.getReportList(request));
    }

    @Operation(summary = "获取举报详情")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/detail")
    public AjaxResult<GetReportDetailResponse> getReportDetail(@RequestParam(value = "id") Long id) {
        return AjaxResult.success(reportService.getReportDetail(id));
    }

    @Operation(summary = "获取举报信息列表")
    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Tag(name = "管理员")
    @GetMapping("/info/list")
    public AjaxResult<BaseListResponse<GetReportInfoElement>> getReportInfoList(@Valid GetReportInfoListRequest request) {
        return AjaxResult.success(reportService.getReportInfoList(request));
    }
}

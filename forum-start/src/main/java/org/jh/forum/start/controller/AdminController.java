package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.ReportService;
import org.jh.forum.common.dto.request.GetReportListRequest;
import org.jh.forum.common.dto.request.HandleReportRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetReportDetailResponse;
import org.jh.forum.common.dto.response.GetReportListElement;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

/**
 * @author MangoGovo
 */
@Slf4j
@RequestMapping("/admin")
@RestController
@Tag(name = "管理员", description = "管理员相关接口")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
public class AdminController {
    @Resource
    private ReportService reportService;

    @PostMapping("/test")
    @Operation(summary = "管理员测试")
    public AjaxResult<Object> test() {
        // 获取当前登录用户ID
        long userId = StpUtil.getLoginIdAsLong();
        log.info("用户ID {}", userId);
        // 获取当前登录用户角色
        List<String> roleList = StpUtil.getRoleList();
        log.info("角色权限列表 {}", roleList);
        return AjaxResult.success();
    }

    @Operation(summary = "处理举报")
    @PostMapping("/report/handle")
    public AjaxResult<Void> handleReport(@Valid @RequestBody HandleReportRequest request) {
        try {
            reportService.handleReport(request);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success();
    }

    @Operation(summary = "获取举报列表")
    @GetMapping("/report/list")
    public AjaxResult<BaseListResponse<GetReportListElement>> getReportList(@Valid GetReportListRequest request) {
        return AjaxResult.success(reportService.getReportList(request));
    }

    @Operation(summary = "获取举报详情")
    @GetMapping("/report/detail")
    public AjaxResult<GetReportDetailResponse> getReportDetail(@RequestParam(value = "report_id", required = true) Long id) {
        GetReportDetailResponse response;
        try {
            response = reportService.getReportDetail(id);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success(response);
    }
}

package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.ReportService;
import org.jh.forum.common.dto.request.ReportContentRequest;
import org.jh.forum.common.dto.request.ReportUserRequest;
import org.jh.forum.common.dto.response.ReportResponse;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * @author zzb
 */
@Slf4j
@RestController
@Tag(name = "举报", description = "举报相关接口")
@RequestMapping("/report")
public class ReportController {
    @Resource
    private ReportService reportService;

    @Operation(summary = "举报用户")
    @PostMapping("/user")
    AjaxResult<ReportResponse> reportUser(@Valid @RequestBody ReportUserRequest request) {
        Long resp;
        try {
            resp = reportService.reportUser(request);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success(ReportResponse.builder().reportId(resp).build());
    }

    @Operation(summary = "举报帖子/评论")
    @PostMapping("/content")
    AjaxResult<ReportResponse> reportContent(@Valid @RequestBody ReportContentRequest request) {
        Long resp;
        try {
            resp = reportService.reportContent(request);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success(ReportResponse.builder().reportId(resp).build());
    }


}

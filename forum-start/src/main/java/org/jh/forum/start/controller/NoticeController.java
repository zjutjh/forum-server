package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.NoticeService;
import org.jh.forum.common.dto.request.GetNoticeListRequest;
import org.jh.forum.common.dto.response.BaseListResponse;
import org.jh.forum.common.dto.response.GetNoticeListElement;
import org.jh.forum.common.dto.response.UnreadCheckResponse;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


/**
 * @author lyyzzz
 */
@Slf4j
@RestController
@RequestMapping("/notices")
@Tag(name = "通知模块")
public class NoticeController {
    @DubboReference
    private NoticeService noticeService;

    @GetMapping("/list")
    @Operation(summary = "查询通知历史")
    public AjaxResult<BaseListResponse<GetNoticeListElement>> getNoticeHistory(@Valid GetNoticeListRequest request) {
        return AjaxResult.success(noticeService.getNoticeList(request));
    }

    @GetMapping("/unread")
    @Operation(summary = "检查未读消息")
    public AjaxResult<UnreadCheckResponse> checkUnreadNotices() {
        return AjaxResult.success(noticeService.unreadCheck());
    }
}
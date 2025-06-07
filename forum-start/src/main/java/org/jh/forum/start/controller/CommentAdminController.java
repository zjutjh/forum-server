package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.request.ChangeCommentStatusRequest;
import org.jh.forum.common.dto.request.GetCommentListAdminRequest;
import org.jh.forum.common.dto.response.GetCommentListAdminResponse;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author qianqianzyk
 */
@Slf4j
@RestController
@RequestMapping("/admin/comment")
@Tag(name = "评论管理")
public class CommentAdminController {
    @Operation(summary = "获取评论列表", description = """
            1. 根据时间顺序排列
            2. 已删除的评论/回复右侧为"恢复"按钮；未删除的评论/回复右侧为"删除"按钮""")
    @GetMapping("/list")
    public AjaxResult<GetCommentListAdminResponse> getCommentListForAdmin(@RequestParam GetCommentListAdminRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "删除/恢复评论")
    @PostMapping("/status")
    public AjaxResult<String> changeCommentStatus(@RequestBody ChangeCommentStatusRequest request) {
        return AjaxResult.success(null);
    }
}

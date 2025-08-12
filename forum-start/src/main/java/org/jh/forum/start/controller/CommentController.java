package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jh.forum.api.dubbo.service.CommentService;
import org.jh.forum.common.annotation.CheckMuted;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * @author qianqianzyk
 */
@Slf4j
@RestController
@RequestMapping("/comment")
@Tag(name = "评论", description = "评论相关接口")
@SaCheckLogin
public class CommentController {
    @DubboReference
    private CommentService commentService;

    @Operation(summary = "发布评论/回复")
    @PostMapping("/publish")
    @CheckMuted
    public AjaxResult<Void> publishComment(@Valid @RequestBody PublishCommentRequest request) {
        commentService.publishComment(request);
        return AjaxResult.success();
    }

    @Operation(summary = "删除评论/回复", description = "仅发布人可删\n级联删除")
    @DeleteMapping("/remove")
    public AjaxResult<Void> deleteComment(@RequestParam(value = "id") Long id) {
        commentService.removeComment(id);
        return AjaxResult.success();
    }

    @Operation(summary = "点赞评论/回复")
    @PostMapping("/upvote")
    public AjaxResult<UpvoteCommentResponse> upvoteComment(@RequestParam(value = "id") Long id) {
        return AjaxResult.success(commentService.upvoteComment(id));
    }

    @Operation(summary = "置顶评论/回复", description = """
                仅帖主设置
                仅允许置顶楼主评论
            """)
    @PostMapping("/pin")
    public AjaxResult<PinCommentResponse> pinComment(@RequestParam(value = "id") Long id) {
        return AjaxResult.success(commentService.pinComment(id));
    }

    @Operation(summary = "获取评论", description = """
                高亮评论会自动排序到第一页第一条（比置顶评论优先级更高）
                每条评论自动获取最热的两条回复
            """)
    @GetMapping("/list")
    public AjaxResult<BaseListResponse<CommentElement>> getCommentList(@Valid GetCommentListRequest request) {
        return AjaxResult.success(commentService.getCommentList(request));
    }

    @Operation(summary = "评论详情页获取回复", description = "高亮回复会自动排序到第一页第一条（比置顶评论优先级更高）")
    @GetMapping("/reply/list")
    public AjaxResult<GetCommentReplyListResponse> getReplyList(@Valid GetReplyListRequest request) {
        return AjaxResult.success(commentService.getReplyList(request));
    }

    @Operation(summary = "获取个人评论")
    @GetMapping("/personal")
    public AjaxResult<BaseListResponse<PersonalCommentListElement>> getPersonalComment(@Valid BaseListRequest request) {
        return AjaxResult.success(commentService.getPersonalCommentList(request));
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Operation(summary = "管理员获取评论列表")
    @Tag(name = "管理员")
    @GetMapping("/admin/list")
    public AjaxResult<BaseListResponse<CommentElement>> getAdminCommentList(@Valid GetCommentListAdminRequest request) {
        return AjaxResult.success(commentService.getAdminCommentList(request));
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Operation(summary = "管理员获取回复列表")
    @Tag(name = "管理员")
    @GetMapping("/admin/reply")
    public AjaxResult<BaseListResponse<ReplyElement>> getAdminReplyList(@Valid GetReplyListAdminRequest request) {
        return AjaxResult.success(commentService.getAdminReplyList(request));
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Operation(summary = "管理员删除/恢复评论")
    @Tag(name = "管理员")
    @PostMapping("/admin/status")
    public AjaxResult<Boolean> adminChangeCommentStatus(@Valid @RequestBody ChangeCommentStatusRequest request) {
        commentService.adminChangeCommentStatus(request);
        return AjaxResult.success();
    }
}

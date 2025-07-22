package org.jh.forum.start.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.CommentService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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
    @Resource
    private CommentService commentService;

    @Operation(summary = "发布评论/回复")
    @PostMapping("/publish")
    public AjaxResult<PublishCommentResponse> publishComment(@RequestBody PublishCommentRequest request) {
        return AjaxResult.success(commentService.publishComment(request));
    }

    @Operation(summary = "删除评论/回复", description = "仅发布人可删\n级联删除")
    @DeleteMapping("/remove")
    public AjaxResult<Void> deleteComment(@RequestParam(value = "comment_id") Long commentId) {
        commentService.removeComment(commentId);
        return AjaxResult.success();
    }

    @Operation(summary = "点赞评论/回复")
    @PostMapping("/upvote")
    public AjaxResult<UpvoteCommentResponse> upvoteComment(@RequestParam(value = "comment_id") Long commentId) {
        return AjaxResult.success(commentService.upvoteComment(commentId));
    }

    @Operation(summary = "置顶评论/回复", description = """
            仅帖主设置
            仅允许置顶楼主评论""")
    @PostMapping("/pin")
    public AjaxResult<PinCommentResponse> pinComment(@RequestParam(value = "comment_id") Long commentId) {
        return AjaxResult.success(commentService.pinComment(commentId));
    }

    @Operation(summary = "获取评论", description = """
            **顺序说明：**
            分为`最新`和`最热`
            `默认`按最热排序（计分=点赞数×1+回复数×2，`按计分由高到低排序`）
            最新`按发布时间由近到远`
                    
            **评论时间说明：**
            1. 1分钟以内---“刚刚”
            2. 10分钟以内---“x分钟前”
            3. 当天---“今天 xx:xx”
            4. 昨天---“昨天 xx:xx”
            5. 昨天前---“xx-xx”（某月某日）
            6. 不是今年---“xxxx-xx-xx”（年月日）
                    
            **多级评论说明：**
            获取时默认每个评论下方展示`一条`该层内`热度最高`的回复
            展开n条评论(n：获取该条评论下方的分级评论条数)""")
    @GetMapping("/list")
    public AjaxResult<GetCommentListResponse> getCommentList(@Valid GetCommentListRequest request) {
        return AjaxResult.success(commentService.getCommentList(request));
    }

    @Operation(summary = "获取回复", description = """
            多级评论
            每次请求获取5条回复信息
            请求成功后，请前端自行减5（展开n条回复的n值）
            回复排序逻辑跟评论排序逻辑保持一致""")
    @GetMapping("/reply/list")
    public AjaxResult<BaseListResponse<ReplyElement>> getReplyList(@Valid GetReplyListRequest request) {
        return AjaxResult.success(commentService.getReplyList(request));
    }

    @Operation(summary = "获取个人评论", description = """
            按时间先后排序
            如果存在多条评论，单独显示一个评论列表，不存在楼层分级情况
            """)
    @GetMapping("/personal")
    public AjaxResult<BaseListResponse<MyCommentElement>> getPersonalComment(@Valid BaseListRequest request) {
        return AjaxResult.success(commentService.getMyCommentList(request));
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Operation(summary = "管理员获取评论列表", description = """
            1. 根据时间顺序排列
            2. 已删除的评论/回复右侧为"恢复"按钮；未删除的评论/回复右侧为"删除"按钮""")
    @Tag(name = "管理员")
    @GetMapping("/admin/list")
    public AjaxResult<BaseListResponse<CommentElement>> getAdminCommentList(@Valid GetCommentListAdminRequest request) {
        return AjaxResult.success(commentService.getAdminCommentList(request));
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Operation(summary = "管理员获取回复列表", description = """
            1. 根据时间顺序排列
            2. 每次请求获取10条回复信息""")
    @Tag(name = "管理员")
    @GetMapping("/admin/reply")
    public AjaxResult<BaseListResponse<ReplyElement>> getAdminReplyList(@Valid GetReplyListAdminRequest request) {
        return AjaxResult.success(commentService.getAdminReplyList(request));
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @Operation(summary = "管理员删除/恢复评论")
    @Tag(name = "管理员")
    @PostMapping("/admin/status")
    public AjaxResult<Boolean> adminChangeCommentStatus(@RequestBody ChangeCommentStatusRequest request) {
        commentService.adminChangeCommentStatus(request);
        return AjaxResult.success();
    }
}

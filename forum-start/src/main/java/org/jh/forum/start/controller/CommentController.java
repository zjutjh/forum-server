package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.api.dubbo.service.CommentService;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.*;
import org.jh.forum.common.exceptions.ApiException;
import org.jh.forum.common.exceptions.ForumServiceException;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * @author qianqianzyk
 */
@Slf4j
@RestController
@RequestMapping("/comment")
@Tag(name = "评论")
public class CommentController {
    @Resource
    private CommentService commentService;

    @Operation(summary = "发布评论/回复")
    @PostMapping("/publish")
    public AjaxResult<PublishCommentResponse> publishComment(@RequestBody PublishCommentRequest request) {
        Long commentId = commentService.publishComment(request);
        PublishCommentResponse response = new PublishCommentResponse();
        response.setCommentId(commentId);
        return AjaxResult.success(response);
    }

    @Operation(summary = "删除评论/回复", description = "仅发布人可删\n级联删除")
    @DeleteMapping("/remove")
    public AjaxResult<Void> deleteComment(@RequestParam RemoveCommentRequest request) {
        try {
            commentService.removeComment(request);
        } catch (ForumServiceException e) {
            throw new ApiException(e);
        }
        return AjaxResult.success();
    }

    @Operation(summary = "点赞评论/回复")
    @PostMapping("/upvote")
    public AjaxResult<UpvoteCommentResponse> upvoteComment(@RequestBody UpvoteCommentRequest request) {
        Boolean status = commentService.upvoteComment(request);
        UpvoteCommentResponse response = new UpvoteCommentResponse();
        response.setStatus(status);
        return AjaxResult.success(response);
    }

    @Operation(summary = "置顶评论/回复", description = "仅帖主设置")
    @PostMapping("/pin")
    public AjaxResult<PinCommentResponse> pinComment(@RequestBody PinCommentRequest request) {
        Boolean status = commentService.pinComment(request);
        PinCommentResponse response = new PinCommentResponse();
        response.setStatus(status);
        return AjaxResult.success(response);
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
    public AjaxResult<GetCommentListResponse> getCommentList(@RequestParam GetCommentListRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "获取回复", description = """
            多级评论
            每次请求获取5条回复信息
            请求成功后，请前端自行减5（展开n条回复的n值）
            回复排序逻辑跟评论排序逻辑保持一致""")
    @GetMapping("/reply/list")
    public AjaxResult<GetReplyListResponse> getReplyList(@RequestParam GetReplyListRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "获取个人评论", description = "按时间先后排序")
    @GetMapping("/personal")
    public AjaxResult<GetPersonaCommentResponse> getPersonalComment(@ModelAttribute GetPersonaCommentRequest request) {
        return AjaxResult.success(null);
    }
}

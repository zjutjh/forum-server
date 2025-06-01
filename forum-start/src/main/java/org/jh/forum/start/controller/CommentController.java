package org.jh.forum.start.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jh.forum.common.dto.request.*;
import org.jh.forum.common.dto.response.GetCommentListResponse;
import org.jh.forum.common.dto.response.GetPersonaCommentResponse;
import org.jh.forum.common.dto.response.GetReplyListResponse;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author qianqianzyk
 */
@Slf4j
@RestController
@RequestMapping("/comment")
@Tag(name = "评论")
public class CommentController {
    @Operation(summary = "发布评论/回复")
    @PostMapping("/publish")
    public AjaxResult<String> publishComment(@RequestBody PublishCommentRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "删除评论/回复",description = "仅发布人可删\n" + "级联删除")
    @DeleteMapping("/delete")
    public AjaxResult<String> deleteComment(@RequestParam("commentId") Integer commentId) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "点赞评论/回复")
    @PostMapping("/upvote")
    public AjaxResult<String> upvoteComment(@RequestBody UpvoteCommentRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "置顶评论/回复",description = "仅帖主设置")
    @PostMapping("/pin")
    public AjaxResult<String> pinComment(@RequestBody PinCommentRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "获取评论",description = "**顺序说明：**\n" +
            "分为`最新`和`最热`\n" +
            "`默认`按最热排序（计分=点赞数×1+回复数×2，`按计分由高到低排序`）\n" +
            "最新`按发布时间由近到远`\n" +
            "\n" +
            "**评论时间说明：**\n" +
            "1. 1分钟以内---“刚刚”\n" +
            "2. 10分钟以内---“x分钟前”\n" +
            "3. 当天---“今天 xx:xx”\n" +
            "4. 昨天---“昨天 xx:xx”\n" +
            "5. 昨天前---“xx-xx”（某月某日）\n" +
            "6. 不是今年---“xxxx-xx-xx”（年月日）\n" +
            "\n" +
            "**多级评论说明：**\n" +
            "获取的时候默认每个评论下方展示`一条`，该层内`热度最高`的回复\n" +
            "展开n条评论(n：获取该条评论下方的分级评论条数)")
    @GetMapping("/list")
    public AjaxResult<GetCommentListResponse> getCommentList(@ModelAttribute GetCommentListRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "获取回复",description = "多级评论\n" +
            "每次请求获取5条回复信息\n" +
            "请求成功后，请前端自行减5（展开n条回复的n值）\n" +
            "回复排序逻辑跟评论排序逻辑保持一致")
    @GetMapping("/reply/list")
    public AjaxResult<GetReplyListResponse> getReplyList(@ModelAttribute GetReplyListRequest request) {
        return AjaxResult.success(null);
    }

    @Operation(summary = "获取个人评论",description = "按时间先后排序")
    @GetMapping("/personal")
    public AjaxResult<GetPersonaCommentResponse> getPersonalComment(@ModelAttribute GetPersonaCommentRequest request) {
        return AjaxResult.success(null);
    }
}

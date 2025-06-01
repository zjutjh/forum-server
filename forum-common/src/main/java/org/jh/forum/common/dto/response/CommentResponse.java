package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.List;

/**
 * @author qianqianzyk
 */
@Data
public class CommentResponse {
    @Schema(description = "评论ID")
    @NotNull
    private Integer id;

    @Schema(description = "用户ID")
    @NotNull
    private Integer userId;

    @Schema(description = "用户昵称")
    @NotBlank
    private String nickname;

    @Schema(description = "用户头像")
    @NotBlank
    private String avatar;

    @Schema(description = "评论内容")
    @NotBlank
    private String content;

    @Schema(description = "附件链接")
    @NotBlank
    private String attachmentUrl;

    @Schema(description = "是否置顶")
    @NotNull
    private Boolean isPinned;

    @Schema(description = "是否为帖主")
    @NotNull
    private Boolean isAuthor;

    @Schema(description = "是否被删除")
    @NotNull
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    @NotBlank
    private String createAt;

    @Schema(description = "点赞数")
    @NotNull
    private Integer upvoteCount;

    @Schema(description = "回复数")
    @NotNull
    private Integer replyCount;

    @Schema(description = "回复列表")
    private List<ReplyResponse> replys;
}

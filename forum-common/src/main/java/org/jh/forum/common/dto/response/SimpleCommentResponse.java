package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class SimpleCommentResponse {
    @Schema(description = "评论ID")
    @NotNull
    private Integer commentId;

    @Schema(description = "评论内容")
    @NotBlank
    private String content;

    @Schema(description = "附件链接")
    @NotBlank
    private String attachmentUrl;

    @Schema(description = "创建时间")
    @NotBlank
    private String createAt;

    @Schema(description = "点赞数")
    @NotNull
    private Integer upvoteCount;

    @Schema(description = "回复数")
    @NotNull
    private Integer replyCount;
}

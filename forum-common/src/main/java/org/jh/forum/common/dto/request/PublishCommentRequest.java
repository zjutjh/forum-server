package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublishCommentRequest {
    @Schema(description = "帖子ID")
    @NotNull
    private Long postId;

    @Schema(description = "父评论ID，即最顶层评论ID，无值即传0")
    @NotNull
    private Long parentId;

    @Schema(description = "回复评论ID，无值即传0")
    @NotNull
    private Long targetId;

    @Schema(description = "评论内容，禁止发空评论")
    @NotBlank
    private String content;

    @Schema(description = "评论附件ID，无值即传0")
    @NotNull
    private Long attachmentId;
}

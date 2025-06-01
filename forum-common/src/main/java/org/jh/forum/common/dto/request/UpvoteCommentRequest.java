package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class UpvoteCommentRequest {
    @Schema(description = "要点赞的评论ID")
    @NotNull
    private Integer commentId;
}

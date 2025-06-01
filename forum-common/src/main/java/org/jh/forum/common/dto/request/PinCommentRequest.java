package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class PinCommentRequest {
    @Schema(description = "要置顶的评论ID")
    @NotNull
    private Integer commentId;
}

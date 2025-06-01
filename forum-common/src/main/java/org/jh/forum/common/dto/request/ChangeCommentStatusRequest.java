package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author qianqianzyk
 */
@Data
public class ChangeCommentStatusRequest {
    @Schema(description = "评论ID")
    @NotNull
    private Integer commentId;

    @Schema(description = "操作类型，1：删除，2：恢复")
    @NotNull
    private Integer status;
}

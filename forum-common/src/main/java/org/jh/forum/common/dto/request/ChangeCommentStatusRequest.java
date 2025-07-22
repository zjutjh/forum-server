package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
public class ChangeCommentStatusRequest {
    @Schema(description = "评论ID")
    @NotNull
    @JsonProperty("comment_id")
    private Long commentId;

    @Schema(description = "操作类型，1: 删除，2: 恢复")
    @NotNull
    private Integer operation;
}

package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
public class PublishCommentResponse {
    @Schema(description = "评论ID")
    @NotNull
    @JsonProperty("comment_id")
    private Long commentId;
}

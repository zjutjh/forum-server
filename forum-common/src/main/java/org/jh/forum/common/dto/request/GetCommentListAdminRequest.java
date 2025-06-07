package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetCommentListAdminRequest extends BaseListRequest {
    @Schema(description = "帖子ID")
    @NotNull
    @JsonProperty("post_id")
    private Long postId;

    @Schema(description = "评论状态，1: 全部，2: 已删，3: 未删")
    @NotNull
    private Integer status;
}

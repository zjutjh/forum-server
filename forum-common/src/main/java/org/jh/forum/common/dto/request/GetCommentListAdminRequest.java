package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetCommentListAdminRequest extends BaseListRequest {
    @Schema(description = "帖子ID")
    @NotNull
    private Long postId;

    @Schema(description = "评论状态，1: 全部，2: 已删，3: 未删")
    @NotNull
    private Integer status;
}

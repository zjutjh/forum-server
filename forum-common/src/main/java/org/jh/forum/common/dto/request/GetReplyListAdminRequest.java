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
public class GetReplyListAdminRequest extends BaseListRequest {
    @Schema(description = "父评论ID")
    @NotNull
    private Long commentId;

    @Schema(description = "评论状态，1: 全部，2: 已删，3: 未删")
    @NotNull
    private Integer status;

    @Schema(description = "该列表中的回复将不会出现在获取到的回复列表中，前端需要持续传值，否则回复列表将会错序")
    private Long[] excludeCommentIds;
}

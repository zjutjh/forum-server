package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.CommentStatusEnum;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetReplyListAdminRequest extends BaseListRequest {
    @Schema(description = "父评论ID")
    @NotNull
    private Long id;

    @Schema(description = "评论状态")
    @NotNull
    private CommentStatusEnum status;
}

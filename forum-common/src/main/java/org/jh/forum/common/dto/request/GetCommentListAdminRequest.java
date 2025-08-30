package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.CommentStatusEnum;

import jakarta.validation.constraints.NotNull;

/**
 * 管理员获取评论列表请求
 *
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetCommentListAdminRequest extends BaseListRequest {
    @Schema(description = "帖子ID")
    @NotNull
    private Long id;

    @Schema(description = "评论状态")
    @NotNull
    private CommentStatusEnum status;
}

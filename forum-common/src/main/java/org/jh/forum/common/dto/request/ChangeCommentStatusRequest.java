package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jh.forum.common.constants.CommentOperationEnum;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 管理员修改评论状态请求
 *
 * @author qianqianzyk
 */
@Data
public class ChangeCommentStatusRequest implements Serializable {
    @Schema(description = "评论或回复ID")
    @NotNull
    private Long id;

    @Schema(description = "操作类型")
    @NotNull
    private CommentOperationEnum operation;
}

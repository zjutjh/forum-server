package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GetCommentListRequest extends BaseListRequest {
    @Parameter(description = "帖子ID")
    @NotNull
    private Integer postId;

    @Parameter(description = "排序方式")
    private String sort;

    @Parameter(description = "高亮评论ID")
    private Integer highlightCommentId;
}

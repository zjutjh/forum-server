package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * 获取评论列表请求
 *
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetCommentListRequest extends BaseListRequest {
    @Schema(description = "帖子ID")
    @NotNull
    private Long id;

    @Schema(description = "排序方式，hot: 按最热，time: 按时间")
    private String sortType;

    @Schema(description = "高亮评论ID，无值即传0")
    private Long highlightCommentId;
}

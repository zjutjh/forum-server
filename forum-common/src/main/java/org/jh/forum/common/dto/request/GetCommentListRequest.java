package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetCommentListRequest extends BaseListRequest {
    @Schema(description = "帖子ID")
    @NotNull
    @JsonProperty("post_id")
    private Long postId;

    @Schema(description = "排序方式，1: 按最热，2: 按时间")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer sort;

    @Schema(description = "高亮评论ID，无值即传0")
    @NotNull
    @JsonProperty("highlight_comment_id")
    private Long highlightCommentId;
}

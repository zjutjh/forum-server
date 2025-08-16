package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetReplyListRequest extends BaseListRequest {
    @Schema(description = "评论或回复ID（如果不是父评论则会自动跳转到父评论）")
    @NotNull
    private Long id;

    @Schema(description = "排序方式，hot: 按最热，time: 按时间")
    @NotBlank
    private String sortType;

    @Schema(description = "高亮回复ID，无值即传0")
    private Long highlightReplyId;
}

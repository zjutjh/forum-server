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
public class GetReplyListRequest extends BaseListRequest {
    @Schema(description = "父评论ID")
    @NotNull
    private Long id;

    @Schema(description = "排序方式，1: 按最热，2: 按时间")
    private Integer sortType;

    @Schema(description = "该列表中的回复将不会出现在获取到的回复列表中，前端需要持续传值，否则回复列表将会错序")
    private Long[] excludeCommentIds;
}

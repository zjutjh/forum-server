package org.jh.forum.common.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetReplyListRequest extends BaseListRequest {
    @Schema(description = "回复ID")
    @NotNull
    @JsonProperty("comment_id")
    private Long commentId;

    @Schema(description = "排序方式，1: 按最热，2: 按时间")
    private Integer sort;
}

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
public class GetReplyListRequest extends BaseListRequest{
    @Parameter(description = "回复ID")
    @NotNull
    private Integer commentId;

    @Parameter(description = "排序方式")
    private String sort;
}

package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块ID（为0则全部帖子）")
    @NotNull
    private Long categoryId;

    @Schema(description = "排序类型（1为最新，2为最热）")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer sortType;
}

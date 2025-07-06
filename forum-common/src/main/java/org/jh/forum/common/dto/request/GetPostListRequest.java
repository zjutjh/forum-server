package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.CategoryEnum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块")
    private CategoryEnum category;

    @Schema(description = "排序类型（1为最新，2为最热）")
    @NotNull
    @Min(1)
    @Max(2)
    private Integer sortType;
}

package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.CategoryEnum;

import jakarta.validation.constraints.NotBlank;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块")
    private CategoryEnum category;

    @Schema(description = "排序类型", allowableValues = {"hot", "new"})
    @NotBlank
    private String sortType;
}

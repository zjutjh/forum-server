package org.jh.forum.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jh.forum.common.constants.PostCategoryEnum;
import org.jh.forum.common.constants.PostSortTypeEnum;

/**
 * 获取帖子列表请求
 *
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块")
    private PostCategoryEnum category;

    @Schema(description = "排序类型")
    private PostSortTypeEnum sortType;
}

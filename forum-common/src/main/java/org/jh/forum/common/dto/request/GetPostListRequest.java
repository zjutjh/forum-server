package org.jh.forum.common.dto.request;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author SugarMGP
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetPostListRequest extends BaseListRequest {
    @Schema(description = "帖子板块ID（为0则全部帖子）")
    @JsonProperty("category_id")
    private Long categoryId;

    @Schema(description = "排序类型（1为最新，2为最热）")
    @JsonProperty("sort_type")
    private Integer sortType;
}

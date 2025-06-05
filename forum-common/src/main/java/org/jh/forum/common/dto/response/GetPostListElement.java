package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetPostListElement {
    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "发帖人ID")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "帖子板块ID")
    @JsonProperty("category_id")
    private Long categoryId;

    @Schema(description = "帖子话题ID列表")
    private Long[] topics;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容（截取50字）")
    private String content;

    @Schema(description = "发帖时间")
    @JsonProperty("created_at")
    private String createdAt;
}

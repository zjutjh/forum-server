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
public class GetMyPostListElement {
    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "帖子板块ID")
    @JsonProperty("category_id")
    private Long categoryId;

    @Schema(description = "帖子话题列表")
    private String[] topics;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容（截取50字）")
    private String content;

    @Schema(description = "帖子点赞数")
    @JsonProperty("like_count")
    private Integer likeCount;

    @Schema(description = "帖子评论数")
    @JsonProperty("comment_count")
    private Integer commentCount;

    @Schema(description = "帖子浏览数")
    @JsonProperty("view_count")
    private Integer viewCount;

    @Schema(description = "发帖时间")
    @JsonProperty("created_at")
    private String createdAt;

    @Schema(description = "帖子是否被置顶")
    @JsonProperty("is_pinned")
    private Boolean isPinned;
}

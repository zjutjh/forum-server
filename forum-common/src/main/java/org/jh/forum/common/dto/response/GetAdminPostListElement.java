package org.jh.forum.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.CategoryEnum;
import org.jh.forum.common.constants.PostStatusEnum;

import java.time.LocalDateTime;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAdminPostListElement {
    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "发帖人")
    private String publisher;

    @Schema(description = "帖子板块")
    private CategoryEnum category;

    @Schema(description = "帖子标题")
    private String title;

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
    private LocalDateTime createdAt;

    @Schema(description = "帖子状态")
    private PostStatusEnum status;

    @Schema(description = "是否置顶（管理员）")
    @JsonProperty("is_pinned")
    private Boolean isPinned;
}

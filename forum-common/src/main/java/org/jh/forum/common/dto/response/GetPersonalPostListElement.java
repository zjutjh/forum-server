package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.constants.CategoryEnum;
import org.jh.forum.common.constants.PostStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetPersonalPostListElement {
    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "帖子板块")
    private CategoryEnum category;

    @Schema(description = "帖子话题列表")
    private List<String> topics;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容（截取50字）")
    private String content;

    @Schema(description = "帖子点赞数")
    private Integer likeCount;

    @Schema(description = "帖子评论数")
    private Integer commentCount;

    @Schema(description = "帖子浏览数")
    private Integer viewCount;

    @Schema(description = "发帖时间")
    private LocalDateTime createdAt;

    @Schema(description = "是否置顶（个人主页）")
    private Boolean isTopped;

    @Schema(description = "帖子状态（此处不会是deleted），如果是查看他人帖子列表则全都是normal")
    private PostStatusEnum status;
}

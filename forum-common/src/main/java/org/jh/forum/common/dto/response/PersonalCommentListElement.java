package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.PictureInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalCommentListElement {
    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "父级评论ID（若没有则为0）")
    private Long parentId;

    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "回复的对象内容（如果有被回复评论则为被回复评论内容，反之则为被回复帖子内容，截取前30个字）")
    private String replyContent;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "附件链接")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "发表时间")
    private LocalDateTime createdAt;

    @Schema(description = "点赞数")
    private Integer upvoteCount;

    @Schema(description = "回复数")
    private Integer replyCount;
}

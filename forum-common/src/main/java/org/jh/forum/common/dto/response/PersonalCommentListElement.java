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

    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "回复ID（若不为回复则该字段为0）")
    private Long replyId;

    @Schema(description = "对象内容（如果有被回复评论则为被回复评论内容，反之则为被回复帖子内容，截取前60个字）")
    private String targetContent;

    @Schema(description = "评论内容（截200字）")
    private String content;

    @Schema(description = "附件链接")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "发表时间")
    private LocalDateTime createdAt;

    @Schema(description = "是否点赞")
    private Boolean isLiked;

    @Schema(description = "点赞数")
    private Integer upvoteCount;

    @Schema(description = "回复数")
    private Integer replyCount;
}

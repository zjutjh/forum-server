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
    @Schema(description = "评论ID")
    private Long commentId;

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

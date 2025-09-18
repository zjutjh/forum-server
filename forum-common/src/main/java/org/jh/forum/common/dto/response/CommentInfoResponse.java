package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息响应
 *
 * @author SugarMGP
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentInfoResponse implements Serializable {
    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "评论人信息")
    private UserInfoDTO publisherInfo;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "附件列表")
    private List<PictureInfoDTO> pictures;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "是否为帖主")
    private Boolean isAuthor;

    @Schema(description = "是否被删除")
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "点赞数")
    private Integer upvoteCount;

    @Schema(description = "回复数")
    private Integer replyCount;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;
}

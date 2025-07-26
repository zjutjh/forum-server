package org.jh.forum.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jh.forum.common.dto.PictureInfoDTO;
import org.jh.forum.common.dto.UserInfoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author qianqianzyk
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentElement {
    @Schema(description = "评论ID")
    @NotNull
    private Long commentId;

    @Schema(description = "评论人信息")
    private UserInfoDTO publisherInfo;

    @Schema(description = "评论内容")
    @NotBlank
    private String content;

    @Schema(description = "附件列表")
    @NotBlank
    private List<PictureInfoDTO> pictures;

    @Schema(description = "是否置顶")
    @NotNull
    private Boolean isPinned;

    @Schema(description = "是否为帖主")
    @NotNull
    private Boolean isAuthor;

    @Schema(description = "是否被删除")
    @NotNull
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    @NotBlank
    private LocalDateTime createdAt;

    @Schema(description = "点赞数")
    @NotNull
    private Integer upvoteCount;

    @Schema(description = "回复数")
    @NotNull
    private Integer replyCount;

    @Schema(description = "回复列表")
    @NotNull
    private List<ReplyElement> replies;
}
